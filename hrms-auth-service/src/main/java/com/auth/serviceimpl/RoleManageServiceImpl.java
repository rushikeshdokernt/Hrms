package com.auth.serviceimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auth.dto.request.AddRoleRequestDto;
import com.auth.dto.request.RolePermissionDto;
import com.auth.dto.response.ApiResponseDto;
import com.auth.dto.response.ModulePermissionsResponseDto;
import com.auth.dto.response.PermissionResponseDto;
import com.auth.dto.response.RolePermissionsResponseDto;
import com.auth.entity.ModuleMaster;
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
public class RoleManageServiceImpl implements RoleManageService {

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
			if (addRoleRequestDto == null || addRoleRequestDto.getRoleName() == null
					|| addRoleRequestDto.getRoleName().trim().isEmpty()) {

				return ResponseEntity.badRequest()
						.body(ApiResponseDto.builder().success(false).message("Role name is required").build());
			}

			// --------------------------------------------------
			// 2. Normalize role name
			// --------------------------------------------------
			String roleName = addRoleRequestDto.getRoleName().trim().toUpperCase();

			// --------------------------------------------------
			// 3. Check role already exists
			// --------------------------------------------------
			if (roleMasterRepository.existsByRoleName(roleName)) {

				return ResponseEntity.status(HttpStatus.CONFLICT)
						.body(ApiResponseDto.builder().success(false).message("Role already exists").build());
			}

			// --------------------------------------------------
			// 4. Create RoleMaster using MapStruct
			// --------------------------------------------------
			addRoleRequestDto.setRoleName(roleName);

			RoleMaster roleMaster = roleMapper.toRoleMaster(addRoleRequestDto);

			roleMaster = roleMasterRepository.save(roleMaster);

			// --------------------------------------------------
			// 5. Process usecase permissions
			// --------------------------------------------------
			Map<UUID, List<String>> usecasePermissions = addRoleRequestDto.getUsecases();

			List<RolePermission> rolePermissions = new ArrayList<>();

			if (usecasePermissions != null && !usecasePermissions.isEmpty()) {

				for (Map.Entry<UUID, List<String>> entry : usecasePermissions.entrySet()) {

					UUID useCaseId = entry.getKey();
					List<String> permissions = entry.getValue();

					// ------------------------------------------
					// Find use case
					// ------------------------------------------
					UseCase useCase = useCaseRepository.findById(useCaseId)
							.orElseThrow(() -> new RuntimeException("Invalid use case: " + useCaseId));

					// ------------------------------------------
					// Convert permission list -> boolean flags
					// ------------------------------------------
					RolePermissionDto permissionDto = buildPermissionDto(permissions);

					// ------------------------------------------
					// MapStruct creates RolePermission
					// ------------------------------------------
					RolePermission rolePermission = roleMapper.toRolePermission(permissionDto);

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

			return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.builder().success(true)
					.message("Role and permissions created successfully").data(data).build());

		} catch (Exception e) {

			log.error("Error occurred in addRoleUsecases API", e);

			throw e;
		}
	}

	private RolePermissionDto buildPermissionDto(List<String> permissions) {

		RolePermissionDto dto = RolePermissionDto.builder().viewAccess(false).createAccess(false).editAccess(false)
				.deleteAccess(false).build();

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
				log.warn("Unknown permission '{}' received", permission);
			}
		}

		return dto;
	}

	// =========================================================

	@Override
	@Transactional(readOnly = true)
	public ResponseEntity<ApiResponseDto> getRolePermissions(
	        HttpServletRequest request,
	        UUID roleId) {

	    log.info(
	            "Started getRolePermissions API for roleId: {}",
	            roleId
	    );

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
	        // 2. Get ALL use cases
	        // ---------------------------------------------

	        List<UseCase> allUseCases =
	                useCaseRepository.findAll();

	        // ---------------------------------------------
	        // 3. Get permissions assigned to this role
	        // ---------------------------------------------

	        List<RolePermission> rolePermissions =
	                rolePermissionRepository
	                        .findByRoleRoleId(roleId);

	        // ---------------------------------------------
	        // 4. Convert role permissions into Map
	        //
	        // key   = useCaseId
	        // value = RolePermission
	        // ---------------------------------------------

	        Map<UUID, RolePermission> permissionMap =
	                rolePermissions.stream()
	                        .collect(Collectors.toMap(
	                                permission ->
	                                        permission.getUseCase()
	                                                .getUseCaseId(),
	                                Function.identity()
	                        ));

	        // ---------------------------------------------
	        // 5. Build permission response for ALL
	        //    use cases
	        // ---------------------------------------------

	        List<PermissionResponseDto> permissionResponses =
	                allUseCases.stream()
	                        .map(useCase -> {

	                            RolePermission rolePermission =
	                                    permissionMap.get(
	                                            useCase.getUseCaseId()
	                                    );

	                            PermissionResponseDto dto =
	                                    new PermissionResponseDto();

	                            // ---------------------------------
	                            // Use case details
	                            // ---------------------------------

	                            dto.setUseCaseId(
	                                    useCase.getUseCaseId()
	                            );

	                            dto.setUseCaseName(
	                                    useCase.getUseCaseName()
	                            );

	                            // ---------------------------------
	                            // Assigned permissions
	                            // ---------------------------------

	                            if (rolePermission != null) {

	                                dto.setViewAccess(
	                                        rolePermission.isViewAccess()
	                                );

	                                dto.setCreateAccess(
	                                        rolePermission.isCreateAccess()
	                                );

	                                dto.setEditAccess(
	                                        rolePermission.isEditAccess()
	                                );

	                                dto.setDeleteAccess(
	                                        rolePermission.isDeleteAccess()
	                                );

	                            } else {

	                                // ---------------------------------
	                                // Use case exists but is NOT assigned
	                                // ---------------------------------

	                                dto.setViewAccess(false);
	                                dto.setCreateAccess(false);
	                                dto.setEditAccess(false);
	                                dto.setDeleteAccess(false);
	                            }

	                            return dto;

	                        })
	                        .toList();

	        // ---------------------------------------------
	        // 6. Group use cases by module
	        // ---------------------------------------------

	        Map<UUID, ModulePermissionsResponseDto> moduleMap =
	                new LinkedHashMap<>();

	        for (int i = 0; i < allUseCases.size(); i++) {

	            UseCase useCase = allUseCases.get(i);

	            PermissionResponseDto permission =
	                    permissionResponses.get(i);

	            ModuleMaster module =
	                    useCase.getModule();

	            if (module == null) {
	                continue;
	            }

	            UUID moduleId =
	                    module.getModuleId();

	            ModulePermissionsResponseDto moduleResponse =
	                    moduleMap.computeIfAbsent(
	                            moduleId,
	                            id -> ModulePermissionsResponseDto
	                                    .builder()
	                                    .moduleId(
	                                            module.getModuleId()
	                                    )
	                                    .moduleName(
	                                            module.getModuleName()
	                                    )
	                                    .usecases(
	                                            new ArrayList<>()
	                                    )
	                                    .build()
	                    );

	            moduleResponse
	                    .getUsecases()
	                    .add(permission);
	        }

	        // ---------------------------------------------
	        // 7. Convert module map to list
	        // ---------------------------------------------

	        List<ModulePermissionsResponseDto> modules =
	                new ArrayList<>(
	                        moduleMap.values()
	                );

	        // ---------------------------------------------
	        // 8. Build response
	        // ---------------------------------------------

	        RolePermissionsResponseDto data =
	                RolePermissionsResponseDto.builder()
	                        .roleId(
	                                roleMaster.getRoleId()
	                        )
	                        .roleName(
	                                roleMaster.getRoleName()
	                        )
	                        .description(
	                                roleMaster.getDescription()
	                        )
	                        .status(
	                                roleMaster.getStatus() != null
	                                        ? roleMaster
	                                                .getStatus()
	                                                .name()
	                                        : null
	                        )
	                        .modules(modules)
	                        .build();

	        // ---------------------------------------------
	        // 9. Return response
	        // ---------------------------------------------

	        return ResponseEntity.ok(
	                ApiResponseDto.builder()
	                        .success(true)
	                        .message(
	                                "Role permissions fetched successfully"
	                        )
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

	@Override
	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity<ApiResponseDto> updateRoleUsecases(HttpServletRequest request, UUID roleId,
			AddRoleRequestDto addRoleRequestDto) {

		log.info("Started updateRoleUsecases API for roleId: {}", roleId);

		try {

			// =========================================================
			// 1. Validate roleId
			// =========================================================

			if (roleId == null) {

				return ResponseEntity.badRequest()
						.body(ApiResponseDto.builder().success(false).message("Role ID is required").build());
			}

			// =========================================================
			// 2. Find existing role
			// =========================================================

			Optional<RoleMaster> roleOptional = roleMasterRepository.findById(roleId);

			if (roleOptional.isEmpty()) {

				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(ApiResponseDto.builder().success(false).message("Role not found").build());
			}

			RoleMaster roleMaster = roleOptional.get();

			// =========================================================
			// 3. Validate request
			// =========================================================

			if (addRoleRequestDto == null) {

				return ResponseEntity.badRequest()
						.body(ApiResponseDto.builder().success(false).message("Request body is required").build());
			}

			// =========================================================
			// 4. Update role name
			//
			// IMPORTANT:
			// We do NOT check "role already exists" here.
			// Same role name is allowed during update.
			// =========================================================

			String existingRoleName = roleMaster.getRoleName();
			String requestedRoleName = addRoleRequestDto.getRoleName();

			if (requestedRoleName != null) {

				requestedRoleName = requestedRoleName.trim();

				if (!requestedRoleName.equals(existingRoleName)) {
					roleMaster.setRoleName(requestedRoleName);
				}
			}

			// =========================================================
			// 5. Get requested usecases
			// =========================================================

			Map<UUID, List<String>> requestedUsecases = addRoleRequestDto.getUsecases();

			if (requestedUsecases == null) {
				requestedUsecases = new HashMap<>();
			}

			// =========================================================
			// 6. Get existing permissions for this role
			// =========================================================

			List<RolePermission> existingPermissions = rolePermissionRepository.findByRoleRoleId(roleId);

			Map<UUID, RolePermission> existingPermissionMap = existingPermissions.stream().collect(
					Collectors.toMap(permission -> permission.getUseCase().getUseCaseId(), Function.identity()));

			// =========================================================
			// 7. Process requested usecases
			// =========================================================

			List<RolePermission> permissionsToSave = new ArrayList<>();

			for (Map.Entry<UUID, List<String>> entry : requestedUsecases.entrySet()) {

				UUID useCaseId = entry.getKey();

				List<String> requestedAccesses = entry.getValue();

				// -----------------------------------------------------
				// Find usecase
				// -----------------------------------------------------

				Optional<UseCase> useCaseOptional = useCaseRepository.findById(useCaseId);

				if (useCaseOptional.isEmpty()) {

					return ResponseEntity.badRequest().body(
							ApiResponseDto.builder().success(false).message("Usecase not found: " + useCaseId).build());
				}

				UseCase useCase = useCaseOptional.get();

				// -----------------------------------------------------
				// If usecase is sent with no access
				// remove existing permission
				// -----------------------------------------------------

				if (requestedAccesses == null || requestedAccesses.isEmpty()) {

					RolePermission existingPermission = existingPermissionMap.get(useCaseId);

					if (existingPermission != null) {

						rolePermissionRepository.delete(existingPermission);
					}

					continue;
				}

				// -----------------------------------------------------
				// Create permission flags
				//
				// Everything starts as FALSE.
				// Only accesses sent in request become TRUE.
				// -----------------------------------------------------

				boolean viewAccess = false;
				boolean createAccess = false;
				boolean editAccess = false;
				boolean deleteAccess = false;

				for (String access : requestedAccesses) {

					if (access == null) {
						continue;
					}

					switch (access.trim().toUpperCase()) {

					case "READ":
					case "VIEW":
						viewAccess = true;
						break;

					case "CREATE":
						createAccess = true;
						break;

					case "EDIT":
					case "UPDATE":
						editAccess = true;
						break;

					case "DELETE":
						deleteAccess = true;
						break;

					default:
						log.warn("Unknown access '{}' for usecase {}", access, useCaseId);
					}
				}

				// -----------------------------------------------------
				// Check whether permission already exists
				// -----------------------------------------------------

				RolePermission rolePermission = existingPermissionMap.get(useCaseId);

				if (rolePermission == null) {

					// =================================================
					// New permission
					// =================================================

					rolePermission = new RolePermission();

					rolePermission.setRole(roleMaster);
					rolePermission.setUseCase(useCase);

				}

				// =====================================================
				// IMPORTANT:
				// Always overwrite ALL permission flags.
				//
				// This removes previously selected permissions that
				// are no longer present in the request.
				// =====================================================

				rolePermission.setViewAccess(viewAccess);
				rolePermission.setCreateAccess(createAccess);
				rolePermission.setEditAccess(editAccess);
				rolePermission.setDeleteAccess(deleteAccess);

				permissionsToSave.add(rolePermission);
			}

			// =========================================================
			// 8. Delete usecases that were NOT sent in request
			// =========================================================

			Set<UUID> requestedUseCaseIds = requestedUsecases.keySet();

			List<RolePermission> permissionsToDelete = existingPermissions.stream().filter(permission -> {

				UUID existingUseCaseId = permission.getUseCase().getUseCaseId();

				return !requestedUseCaseIds.contains(existingUseCaseId);
			}).toList();

			if (!permissionsToDelete.isEmpty()) {

				rolePermissionRepository.deleteAll(permissionsToDelete);
			}

			// =========================================================
			// 9. Save updated/new permissions
			// =========================================================

			if (!permissionsToSave.isEmpty()) {

				rolePermissionRepository.saveAll(permissionsToSave);
			}

			// =========================================================
			// 10. Response
			// =========================================================

			Map<String, Object> responseData = new HashMap<>();

			responseData.put("roleId", roleMaster.getRoleId());

			responseData.put("roleName", roleMaster.getRoleName());

			responseData.put("updatedUsecases", permissionsToSave.size());

			responseData.put("removedUsecases", permissionsToDelete.size());

			log.info("Role updated successfully. roleId: {}", roleId);

			return ResponseEntity.ok(ApiResponseDto.builder().success(true)
					.message("Role and permissions updated successfully").data(responseData).build());

		} catch (Exception e) {

			log.error("Error while updating role. roleId: {}", roleId, e);

			throw e;
		}
	}

	@Override
	public ResponseEntity<ApiResponseDto> getRoleList() {

		log.info("Started getRoleList API");

		List<RoleMaster> roleList = roleMasterRepository.findAll();

		List<RolePermissionsResponseDto> roles = roleList.stream().map(roleMapper::toRoleResponse).toList();

		return ResponseEntity
				.ok(ApiResponseDto.builder().success(true).message("Roles fetched successfully").data(roles).build());
	}

	@Override
	public ResponseEntity<ApiResponseDto> getRoleDropdownList() {

		log.info("Started getRoleList API");

		List<RoleMaster> roleList = roleMasterRepository.findAll();

		List<RolePermissionsResponseDto> roles = roleList.stream().map(roleMapper::toRoleDropdwonResponse).toList();

		return ResponseEntity
				.ok(ApiResponseDto.builder().success(true).message("Roles fetched successfully").data(roles).build());
	}

}
