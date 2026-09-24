package com.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RolePermissionsResponseDto {
	private UUID roleId;
	private String roleName;
	private String description;
	private String status;
	private List<PermissionResponseDto> permissions;
}
