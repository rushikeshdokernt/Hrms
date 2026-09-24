package com.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionResponseDto {
	private UUID rolePermissionId;
	private UUID useCaseId;
	private String useCaseName;
	private boolean viewAccess;
	private boolean createAccess;
	private boolean editAccess;
	private boolean deleteAccess;
}
