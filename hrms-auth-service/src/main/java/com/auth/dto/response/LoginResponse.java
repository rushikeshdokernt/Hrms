package com.auth.dto.response;

import java.util.UUID;

import com.auth.entity.RefreshToken;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {

	private String accessToken;
	
	private String refreshToken;
	
	private UUID userAccountId;
	
	private String userName;
	
	private String email;
	
	private String roleName;
	
}
