package com.auth.serviceimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auth.dto.request.AddRoleRequestDto;
import com.auth.dto.request.RolePermissionDto;
import com.auth.dto.response.ApiResponseDto;
import com.auth.dto.response.PermissionResponseDto;
import com.auth.dto.response.RolePermissionsResponseDto;
import com.auth.entity.RoleMaster;
import com.auth.entity.RolePermission;
import com.auth.entity.UseCase;
import com.auth.mapper.RoleMapper;
import com.auth.repository.RoleMasterRepository;
import com.auth.repository.RolePermissionRepository;
import com.auth.repository.UseCaseRepository;
import com.auth.service.RoleManageService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoleManageServiceImpl implements RoleManageService{
	

	private final RoleMasterRepository roleMasterRepository;
	
	private final RoleMapper roleMapper;
	
	private final UseCaseRepository useCaseRepository;
	
	private final RolePermissionRepository rolePermissionRepository;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity<ApiResponseDto> addRoleUsecases(HttpServletRequest request,
	        AddRoleRequestDto addRoleRequestDto) {

	    log.info("Started addRoleUsecases API");

	    try {

	        // --------------------------------------------------
	        // 1. Validate role name
	        // --------------------------------------------------
	        if (addRoleRequestDto == null
	                || addRoleRequestDto.getRoleName() == null
	                || addRoleRequestDto.getRoleName().trim().isEmpty()) {

	            return ResponseEntity.badRequest()
	                    .body(ApiResponseDto.builder()
	                            .success(false)
	                            .message("Role name is required")
	                            .build());
	        }

	        // --------------------------------------------------
	        // 2. Normalize role name
	        // --------------------------------------------------
	        String roleName = addRoleRequestDto
	                .getRoleName()
	                .trim()
	                .toUpperCase();

	        // --------------------------------------------------
	        // 3. Check role already exists
	        // --------------------------------------------------
	        if (roleMasterRepository.existsByRoleName(roleName)) {

	            return ResponseEntity.status(HttpStatus.CONFLICT)
	                    .body(ApiResponseDto.builder()
	                            .success(false)
	                            .message("Role already exists")
	                            .build());
	        }

	        // --------------------------------------------------
	        // 4. Create RoleMaster using MapStruct
	        // --------------------------------------------------
	        addRoleRequestDto.setRoleName(roleName);

	        RoleMaster roleMaster =
	                roleMapper.toRoleMaster(addRoleRequestDto);

	        roleMaster = roleMasterRepository.save(roleMaster);

	        // --------------------------------------------------
	        // 5. Process usecase permissions
	        // --------------------------------------------------
	        Map<UUID, List<String>> usecasePermissions =
	                addRoleRequestDto.getUsecases();

	        List<RolePermission> rolePermissions =
	                new ArrayList<>();

	        if (usecasePermissions != null
	                && !usecasePermissions.isEmpty()) {

	            for (Map.Entry<UUID, List<String>> entry
	                    : usecasePermissions.entrySet()) {

	                UUID useCaseId = entry.getKey();
	                List<String> permissions = entry.getValue();

	                // ------------------------------------------
	                // Find use case
	                // ------------------------------------------
	                UseCase useCase = useCaseRepository
	                        .findById(useCaseId)
	                        .orElseThrow(() ->
	                                new RuntimeException(
	                                        "Invalid use case: " + useCaseId
	                                )
	                        );

	                // ------------------------------------------
	                // Convert permission list -> boolean flags
	                // ------------------------------------------
	                RolePermissionDto permissionDto =
	                        buildPermissionDto(permissions);

	                // ------------------------------------------
	                // MapStruct creates RolePermission
	                // ------------------------------------------
	                RolePermission rolePermission =
	                        roleMapper.toRolePermission(permissionDto);

	                rolePermission.setRole(roleMaster);
	                rolePermission.setUseCase(useCase);

	                rolePermissions.add(rolePermission);
	            }
	        }

	        // --------------------------------------------------
	        // 6. Save permissions
	        // --------------------------------------------------
	        if (!rolePermissions.isEmpty()) {
	            rolePermissionRepository.saveAll(rolePermissions);
	        }

	        // --------------------------------------------------
	        // 7. Response
	        // --------------------------------------------------
	        Map<String, Object> data = new HashMap<>();

	        data.put("roleId", roleMaster.getRoleId());
	        data.put("roleName", roleMaster.getRoleName());
	        data.put("permissionsCreated", rolePermissions.size());

	        return ResponseEntity.status(HttpStatus.CREATED)
	                .body(ApiResponseDto.builder()
	                        .success(true)
	                        .message("Role and permissions created successfully")
	                        .data(data)
	                        .build());

	    } catch (Exception e) {

	        log.error(
	                "Error occurred in addRoleUsecases API",
	                e
	        );

	        throw e;
	    }
	}
	
	private RolePermissionDto buildPermissionDto(
	        List<String> permissions) {

	    RolePermissionDto dto = RolePermissionDto.builder()
	            .viewAccess(false)
	            .createAccess(false)
	            .editAccess(false)
	            .deleteAccess(false)
	            .build();

	    if (permissions == null || permissions.isEmpty()) {
	        return dto;
	    }

	    for (String permission : permissions) {

	        if (permission == null) {
	            continue;
	        }

	        switch (permission.trim().toUpperCase()) {

	            case "READ":
	            case "VIEW":
	                dto.setViewAccess(true);
	                break;

	            case "WRITE":
	            case "CREATE":
	                dto.setCreateAccess(true);
	                break;

	            case "EDIT":
	                dto.setEditAccess(true);
	                break;

	            case "DELETE":
	                dto.setDeleteAccess(true);
	                break;

	            default:
	                log.warn(
	                        "Unknown permission '{}' received",
	                        permission
	                );
	        }
	    }

	    return dto;
	}
	
	
	
	
	//=========================================================
	
	
	
	@Override
	@Transactional(readOnly = true)
	public ResponseEntity<ApiResponseDto> getRolePermissions(
	        HttpServletRequest request,
	        UUID roleId) {

	    log.info("Started getRolePermissions API for roleId: {}", roleId);

	    try {

	        // ---------------------------------------------
	        // 1. Find role
	        // ---------------------------------------------
	        RoleMaster roleMaster = roleMasterRepository
	                .findById(roleId)
	                .orElseThrow(() ->
	                        new RuntimeException(
	                                "Role not found: " + roleId
	                        )
	                );

	        // ---------------------------------------------
	        // 2. Find role permissions
	        // ---------------------------------------------
	        List<RolePermission> rolePermissions =
	                rolePermissionRepository
	                        .findByRoleRoleId(roleId);

	        // ---------------------------------------------
	        // 3. Map permissions using MapStruct
	        // ---------------------------------------------
	        List<PermissionResponseDto> permissions =
	                rolePermissions.stream()
	                        .map(roleMapper::toPermissionResponse)
	                        .toList();

	        // ---------------------------------------------
	        // 4. Build response
	        // ---------------------------------------------
	        RolePermissionsResponseDto data =
	                RolePermissionsResponseDto.builder()
	                        .roleId(roleMaster.getRoleId())
	                        .roleName(roleMaster.getRoleName())
	                        .description(roleMaster.getDescription())
	                        .status(
	                                roleMaster.getStatus() != null
	                                        ? roleMaster.getStatus().name()
	                                        : null
	                        )
	                        .permissions(permissions)
	                        .build();

	        return ResponseEntity.ok(
	                ApiResponseDto.builder()
	                        .success(true)
	                        .message("Role permissions fetched successfully")
	                        .data(data)
	                        .build()
	        );

	    } catch (Exception e) {

	        log.error(
	                "Error occurred in getRolePermissions API for roleId: {}",
	                roleId,
	                e
	        );

	        throw e;
	    }
	}

}
