package com.auth.service;

import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.auth.dto.request.AddRoleRequestDto;
import com.auth.dto.response.ApiResponseDto;

import jakarta.servlet.http.HttpServletRequest;

public interface RoleManageService {

	ResponseEntity<ApiResponseDto> addRoleUsecases(HttpServletRequest request, AddRoleRequestDto addRoleRequestDto);

	ResponseEntity<ApiResponseDto> getRolePermissions(HttpServletRequest request, UUID roleId);

}
