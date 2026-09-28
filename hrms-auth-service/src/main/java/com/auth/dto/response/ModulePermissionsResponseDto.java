package com.auth.dto.response;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModulePermissionsResponseDto {

    private UUID moduleId;

    private String moduleName;

    private List<PermissionResponseDto> usecases;
}
