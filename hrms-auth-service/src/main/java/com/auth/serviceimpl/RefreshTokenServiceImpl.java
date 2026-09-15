package com.auth.serviceimpl;


import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.auth.dto.request.LogoutRequestDto;
import com.auth.dto.response.ApiResponseDto;
import com.auth.entity.RefreshToken;
import com.auth.entity.UserAccounts;
import com.auth.exception.BadRequestException;
import com.auth.exception.ResourceNotFoundException;
import com.auth.repository.RefreshTokenRepository;
import com.auth.service.RefreshTokenService;



@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

	@Autowired
    private  RefreshTokenRepository refreshTokenRepository;
	

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    @Override
    public RefreshToken createRefreshToken(UserAccounts user) {

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUserAccounts(user);
        refreshToken.setExpiryDate(
        	    OffsetDateTime.now().plus(refreshExpiration, ChronoUnit.MILLIS)
        	);
        refreshToken.setRevoked(false);

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public RefreshToken verifyRefreshToken(String token) {

        RefreshToken refreshToken =
                refreshTokenRepository
                        .findByToken(token)
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Invalid refresh token."));

        if (refreshToken.isRevoked()) {
            throw new BadRequestException(
                    "Refresh token has been revoked.");
        }

        if (refreshToken.getExpiryDate()
                .isBefore(OffsetDateTime.now())) {

            throw new BadRequestException(
                    "Refresh token has expired.");
        }

        return refreshToken;
    }
    
    @Override
    public ResponseEntity<ApiResponseDto> refreshLogout(LogoutRequestDto request) {

        revokeRefreshToken(
                request.getRefreshToken());

        return ResponseEntity.ok(
                ApiResponseDto.builder()
                        .success(true)
                        .message("Logged out successfully")
                        .build());
    }

    @Override
    public void revokeRefreshToken(String token) {

        RefreshToken refreshToken =
                refreshTokenRepository.findByToken(token)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Refresh token not found"));

        refreshToken.setRevoked(true);

        refreshTokenRepository.save(refreshToken);
    }

    @Override
    public void revokeAllUserTokens(UserAccounts user) {

        List<RefreshToken> tokens =
                refreshTokenRepository.findByUserAccounts(user);

        for (RefreshToken token : tokens) {

            token.setRevoked(true);
        }

        refreshTokenRepository.saveAll(tokens);
    }
}
