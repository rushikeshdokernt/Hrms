package com.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth.dto.request.AddRoleRequestDto;
import com.auth.dto.response.ApiResponseDto;
import com.auth.service.RoleManageService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import static com.auth.constant.ApiConstants.AUTH;
import static com.auth.constant.ApiConstants.ROLE;
import static com.auth.constant.ApiConstants.ROLE_PERMISSION_BY_ID;

import java.util.UUID;

@RestController
@RequestMapping(AUTH + ROLE)
@RequiredArgsConstructor
public class RoleManageConatroller {

	private final RoleManageService roleManageService;

	@PostMapping
	public ResponseEntity<ApiResponseDto> addRoleUsecases(HttpServletRequest request,
			@RequestBody AddRoleRequestDto addRoleRequestDto) {
		return roleManageService.addRoleUsecases(request, addRoleRequestDto);
	}

	@GetMapping(ROLE_PERMISSION_BY_ID)
	public ResponseEntity<ApiResponseDto> getRolePermissions(HttpServletRequest request, @PathVariable UUID roleId) {
		return roleManageService.getRolePermissions(request, roleId);
	}
}
