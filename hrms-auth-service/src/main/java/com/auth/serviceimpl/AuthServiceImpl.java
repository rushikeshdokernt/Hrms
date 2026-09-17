package com.auth.serviceimpl;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.auth.controller.TenantDetails;
import com.auth.dto.request.RefreshTokenRequest;
import com.auth.dto.request.SuperAdminRegisterRequest;
import com.auth.dto.response.ApiResponseDto;
import com.auth.dto.response.LoginResponse;
import com.auth.entity.RefreshToken;
import com.auth.entity.RoleMaster;
import com.auth.entity.UserAccounts;
import com.auth.entity.UserRole;
import com.auth.enums.RoleStatus;
import com.auth.exception.BadRequestException;
import com.auth.exception.DuplicateResourceException;
import com.auth.exception.InvalidCredentialsException;
import com.auth.exception.InvalidRequestException;
import com.auth.exception.ResourceNotFoundException;
import com.auth.jwt.security.JwtService;
import com.auth.mapper.UserAccountsMapper;
import com.auth.repository.RefreshTokenRepository;
import com.auth.repository.RoleMasterRepository;
import com.auth.repository.TenantDetailsRepository;
import com.auth.repository.UserAccountsRepository;
import com.auth.repository.UserRoleRepository;
import com.auth.service.AuthService;
import com.auth.service.RefreshTokenService;
import com.fasterxml.jackson.databind.JsonNode;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{
	
	private final UserAccountsMapper userAccountsMapper;
	
	private final UserAccountsRepository userAccountsRepository;
	
	private final RoleMasterRepository roleMasterRepository;
	
	private final UserRoleRepository userRoleRepository;
	
	private final PasswordEncoder passwordEncoder;
	
	private final JwtService jwtService;
	
	private final RefreshTokenService refreshTokenService;
	
	private final RefreshTokenRepository refreshTokenRepository;
	
	private final TenantDetailsRepository tenantDetailsRepository;

	private final com.auth.external.tenant.util.TenantServiceUtil tenantServiceUtil;

	@Transactional
	@Override
	public ResponseEntity<ApiResponseDto> registerSuperAdmin(
	        SuperAdminRegisterRequest request) {

	    if (userAccountsRepository.existsByEmail(request.getEmail())) {
	        throw new DuplicateResourceException(
	                "Email already exists: " + request.getEmail()
	        );
	    }

	    RoleMaster roleMaster = roleMasterRepository
	            .findByRoleName("SUPER_ADMIN")
	            .orElseGet(() -> {

	                RoleMaster role = new RoleMaster();

	                role.setRoleName("SUPER_ADMIN");
	                role.setCreatedDate(OffsetDateTime.now());
	                role.setStatus(RoleStatus.ACTIVE);

	                return roleMasterRepository.save(role);
	            });

	    

	    UserAccounts userAccount =
	            userAccountsMapper.toEntity(request);

	    userAccount.setPassword(
	            passwordEncoder.encode(request.getPassword())
	    );


	    userAccount = userAccountsRepository.save(userAccount);

	    UserRole userRole = new UserRole();

	    userRole.setUserAccounts(userAccount);
	    userRole.setRoleMaster(roleMaster);

	    userRoleRepository.save(userRole);

	    ApiResponseDto response = ApiResponseDto.builder()
	            .success(true)
	            .message("Superadmin created successfully")
	            .build();

	    return ResponseEntity
	            .status(HttpStatus.CREATED)
	            .body(response);
	}

	@Override
	public ResponseEntity<ApiResponseDto> login(JsonNode loginRequest) {

	    String password = loginRequest.path("password").asText(null);

	    if (password == null || password.isBlank()) {
	        throw new InvalidRequestException("Password is required");
	    }

	    // Check if tenant was passed in request body
	    if (com.auth.multitenancy.TenantContext.getCurrentTenant() == null) {
	        String reqTenantId = loginRequest.path("tenantId").asText(null);
	        String reqTenantCode = loginRequest.path("tenantCode").asText(null);
	        String reqSubdomain = loginRequest.path("subdomain").asText(null);

	        String targetTenant = (reqTenantId != null && !reqTenantId.isBlank()) ? reqTenantId :
	                (reqTenantCode != null && !reqTenantCode.isBlank()) ? reqTenantCode : reqSubdomain;

	        if (targetTenant != null && !targetTenant.isBlank()) {
	            com.auth.multitenancy.TenantContext.setCurrentTenant(targetTenant);
	        }
	    }

	    UserAccounts userAccount;

//	    if (loginRequest.hasNonNull("username")) {
//
//	        String username = loginRequest.path("username").asText();
//
//	        if (username.isBlank()) {
//	            throw new InvalidRequestException("Username is required");
//	        }
//
//	        userAccount = userAccountsRepository
//	                .findByUsername(username)
//	                .orElseThrow(() ->
//	                        new InvalidCredentialsException(
//	                                "Invalid username or password"));
//
//	    } 
	     if (loginRequest.hasNonNull("email")) {

	        String email = loginRequest.path("email").asText();

	        if (email.isBlank()) {
	            throw new InvalidRequestException("Email is required");
	        }

	        userAccount = userAccountsRepository
	                .findByEmail(email)
	                .orElseThrow(() ->
	                        new InvalidCredentialsException(
	                                "Invalid email or password"));

	    } 
//	    else if (loginRequest.hasNonNull("contact")) {
//
//	        String contact = loginRequest.path("contact").asText();
//
//	        if (contact.isBlank()) {
//	            throw new InvalidRequestException("Contact is required");
//	        }
//
//	        userAccount = userAccountsRepository
//	                .findByContact(contact)
//	                .orElseThrow(() ->
//	                        new InvalidCredentialsException(
//	                                "Invalid contact or password"));
//
//	    }
	    else {
	        throw new InvalidRequestException(
	                "Username, email or contact is required");
	    }

	    if (!passwordEncoder.matches(
	            password,
	            userAccount.getPassword())) {

	        throw new InvalidCredentialsException(
	                "Invalid credentials");
	    }

	    UserRole userRole = userRoleRepository
	            .findByUserAccountsUserAccountId(
	                    userAccount.getUserAccountId())
	            .orElseThrow(() ->
	                    new ResourceNotFoundException(
	                            "Role not assigned to user"));

	    RoleMaster roleMaster = userRole.getRoleMaster();
	    
	    TenantDetails tenantDetails = resolveCurrentTenantDetails();

	    String token = jwtService.generateToken(
	    		userRole,tenantDetails
	    );
	    
	    RefreshToken refreshToken = refreshTokenService.createRefreshToken(userAccount);

	    // 6. Response
//	    Map<String, Object> data = new HashMap<>();
//
//	    data.put("accessToken", token);
//	    data.put("tokenType", "Bearer");
//	    data.put("userAccountId",
//	            userAccount.getUserAccountId());
//	    data.put("username",
//	            userAccount.getUsername());
//	    data.put("email",
//	            userAccount.getEmail());
//	    data.put("roleName",
//	            roleMaster.getRoleName());
	    
	    LoginResponse loginResponse = LoginResponse.builder()
	            .accessToken(token)
	            .refreshToken(refreshToken.getToken())
	            .build();
	    		
	    		

	    return ResponseEntity.ok(
	            ApiResponseDto.builder()
	                    .success(true)
	                    .message("Login successful")
	                    .data(loginResponse)
	                    .build()
	    );
	}

	@Override
	public ResponseEntity<ApiResponseDto> refreshToken(RefreshTokenRequest request) {
		UUID userId = null;
		try {
			if (request == null || request.getRefreshToken() == null || request.getRefreshToken().isBlank()) {
				throw new BadRequestException("Refresh token is required.");
			}

			// Validate Refresh Token
			RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(request.getRefreshToken());

			UserAccounts user = refreshToken.getUserAccounts();
			if (user != null) {
				userId = user.getUserAccountId();
			}

			UserRole userRole = userRoleRepository.findByUserAccountsUserAccountId(user.getUserAccountId())
					.orElseThrow(() -> new ResourceNotFoundException("User role not found"));

			
			TenantDetails tenantDetails = resolveCurrentTenantDetails();

			// Generate new Access Token
			String accessToken = jwtService.generateToken(userRole,tenantDetails);

			// ===== Refresh Token Rotation =====

			refreshToken.setRevoked(true);
			refreshTokenRepository.save(refreshToken);

			RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);

			//logAudit(userId, orgId, AuditCategory.SECURITY, AuditAction.REFRESH_TOKEN, AuditStatus.SUCCESS, "REFRESH_TOKEN", "Token refreshed successfully.");

			Map<String, Object> data = new HashMap<>();
			data.put("accessToken", accessToken);
			data.put("refreshToken", newRefreshToken.getToken());

			return ResponseEntity
					.ok(ApiResponseDto.builder().success(true).message("Token refreshed successfully.").data(data).build());
		} catch (Exception ex) {
			//logAudit(userId, orgId, AuditCategory.SECURITY, AuditAction.REFRESH_TOKEN, AuditStatus.FAILED, "REFRESH_TOKEN", ex.getMessage());
			throw ex;
		}
	}

	private TenantDetails resolveCurrentTenantDetails() {
		String currentTenant = com.auth.multitenancy.TenantContext.getCurrentTenant();
		if (currentTenant != null && !currentTenant.isBlank()) {
			try {
				com.auth.dto.response.TenantConnectionConfigDto config = null;
				if (currentTenant.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")) {
					config = tenantServiceUtil.getTenantById(currentTenant);
				} else {
					config = tenantServiceUtil.getTenantByCode(currentTenant);
					if (config == null) {
						config = tenantServiceUtil.getTenantBySubdomain(currentTenant);
					}
				}
				if (config != null) {
					return TenantDetails.builder()
							.tenantId(config.getTenantId())
							.tenantName(config.getTenantName())
							.build();
				}
			} catch (Exception ignored) {
			}
		}
		return tenantDetailsRepository.findFirstByOrderByTenantDetailIdAsc()
				.orElseGet(() -> TenantDetails.builder().tenantName("Default").build());
	}

}
