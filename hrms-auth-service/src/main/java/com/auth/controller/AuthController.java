package com.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth.dto.request.RefreshTokenRequest;
import com.auth.dto.request.SuperAdminRegisterRequest;
import com.auth.dto.response.ApiResponseDto;
import com.auth.service.AuthService;
import com.fasterxml.jackson.databind.JsonNode;

import static com.auth.constant.ApiConstants.AUTH;
import static com.auth.constant.ApiConstants.SUPER_ADMIN_REGISTER;
import static com.auth.constant.ApiConstants.LOGIN;
import static com.auth.constant.ApiConstants.REFRESH_TOKEN;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(AUTH)
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping(SUPER_ADMIN_REGISTER)
	public ResponseEntity<ApiResponseDto> registerSuperAdmin(
			@RequestBody SuperAdminRegisterRequest superAdminRegisterRequest) {

		return authService.registerSuperAdmin(superAdminRegisterRequest);
	}

	@PostMapping(LOGIN)
	public ResponseEntity<ApiResponseDto> login(
	        @RequestBody JsonNode loginRequest) {

	    return authService.login(loginRequest);
	}
	
	
	@PostMapping(REFRESH_TOKEN)
	public ResponseEntity<ApiResponseDto> refreshToken(
	        @RequestBody RefreshTokenRequest request) {

	    return authService.refreshToken(request);
	}
	
	


}
