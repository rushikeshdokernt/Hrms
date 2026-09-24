package com.auth.mapper;

import com.auth.dto.request.AddRoleRequestDto;
import com.auth.dto.request.RolePermissionDto;
import com.auth.dto.response.PermissionResponseDto;
import com.auth.dto.response.RolePermissionsResponseDto;
import com.auth.entity.RoleMaster;
import com.auth.entity.RolePermission;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {

	@Mapping(target = "roleId", ignore = true)
	@Mapping(target = "roleName", expression = "java(toUpperCase(request.getRoleName()))")
	@Mapping(target = "status", ignore = true)
	@Mapping(target = "additionDetail", ignore = true)
	RoleMaster toRoleMaster(AddRoleRequestDto request);

	@Mapping(target = "rolePermissionId", ignore = true)
	@Mapping(target = "role", ignore = true)
	@Mapping(target = "useCase", ignore = true)
	@Mapping(target = "viewAccess", source = "viewAccess")
	@Mapping(target = "createAccess", source = "createAccess")
	@Mapping(target = "editAccess", source = "editAccess")
	@Mapping(target = "deleteAccess", source = "deleteAccess")
	@Mapping(target = "additionDetail", ignore = true)
	RolePermission toRolePermission(RolePermissionDto dto);

	@Mapping(target = "useCaseId", source = "useCase.useCaseId")
	@Mapping(target = "useCaseName", source = "useCase.useCaseName")
	PermissionResponseDto toPermissionResponse(RolePermission rolePermission);

	default String toUpperCase(String value) {
		return value == null ? null : value.trim().toUpperCase();
	}

}
