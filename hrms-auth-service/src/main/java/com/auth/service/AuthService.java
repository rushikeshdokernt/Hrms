package com.auth.service;

import org.springframework.http.ResponseEntity;

import com.auth.dto.request.RefreshTokenRequest;
import com.auth.dto.request.SuperAdminRegisterRequest;
import com.auth.dto.response.ApiResponseDto;
import com.fasterxml.jackson.databind.JsonNode;

public interface AuthService {

	ResponseEntity<ApiResponseDto> registerSuperAdmin(SuperAdminRegisterRequest superAdminRegisterRequest);

	ResponseEntity<ApiResponseDto> login(JsonNode loginRequest);

	ResponseEntity<ApiResponseDto> refreshToken(RefreshTokenRequest request);

}
