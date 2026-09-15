package com.auth.service;

import org.springframework.http.ResponseEntity;

import com.auth.dto.request.LogoutRequestDto;
import com.auth.dto.response.ApiResponseDto;
import com.auth.entity.RefreshToken;
import com.auth.entity.UserAccounts;

public interface RefreshTokenService {

	public RefreshToken createRefreshToken(UserAccounts userAccounts);

	public RefreshToken verifyRefreshToken(String token);

	public void revokeRefreshToken(String token);

	public void revokeAllUserTokens(UserAccounts userAccounts);
	
	public ResponseEntity<ApiResponseDto> refreshLogout(LogoutRequestDto request);
}
