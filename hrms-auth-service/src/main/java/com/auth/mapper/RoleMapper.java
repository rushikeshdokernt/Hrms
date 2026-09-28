package com.auth.mapper;

import com.auth.dto.request.AddRoleRequestDto;
import com.auth.dto.request.RolePermissionDto;
import com.auth.dto.response.PermissionResponseDto;
import com.auth.dto.response.RolePermissionsResponseDto;
import com.auth.entity.RoleMaster;
import com.auth.entity.RolePermission;
import com.auth.entity.UseCase;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mapping(target = "roleId", ignore = true)
    @Mapping(
            target = "roleName",
            expression = "java(toUpperCase(request.getRoleName()))"
    )
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


    // Existing mapping - still useful
    @Mapping(
            target = "useCaseId",
            source = "useCase.useCaseId"
    )
    @Mapping(
            target = "useCaseName",
            source = "useCase.useCaseName"
    )
    PermissionResponseDto toPermissionResponse(
            RolePermission rolePermission
    );


    // NEW
    // Used when a usecase exists but is not assigned to the role
    @Mapping(
            target = "useCaseId",
            source = "useCaseId"
    )
    @Mapping(
            target = "useCaseName",
            source = "useCaseName"
    )
    @Mapping(target = "viewAccess", constant = "false")
    @Mapping(target = "createAccess", constant = "false")
    @Mapping(target = "editAccess", constant = "false")
    @Mapping(target = "deleteAccess", constant = "false")
    PermissionResponseDto toUnassignedPermissionResponse(
            UseCase useCase
    );


    @Mapping(target = "modules", ignore = true)
    RolePermissionsResponseDto toRoleResponse(
            RoleMaster roleMaster
    );


    @Mapping(target = "modules", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "status", ignore = true)
    RolePermissionsResponseDto toRoleDropdwonResponse(
            RoleMaster roleMaster
    );


    default String toUpperCase(String value) {
        return value == null
                ? null
                : value.trim().toUpperCase();
    }
}
