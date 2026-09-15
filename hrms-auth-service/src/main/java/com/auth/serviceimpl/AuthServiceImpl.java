package com.auth.serviceimpl;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.auth.dto.request.SuperAdminRegisterRequest;
import com.auth.dto.response.ApiResponseDto;
import com.auth.entity.RoleMaster;
import com.auth.entity.UserAccounts;
import com.auth.entity.UserRole;
import com.auth.enums.RoleStatus;
import com.auth.exception.DuplicateResourceException;
import com.auth.exception.InvalidCredentialsException;
import com.auth.exception.InvalidRequestException;
import com.auth.exception.ResourceNotFoundException;
import com.auth.jwt.security.JwtService;
import com.auth.mapper.UserAccountsMapper;
import com.auth.repository.RoleMasterRepository;
import com.auth.repository.UserAccountsRepository;
import com.auth.repository.UserRoleRepository;
import com.auth.service.AuthService;
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

	    userRole.setUserAccount(userAccount);
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
	            .findByUserAccountUserAccountId(
	                    userAccount.getUserAccountId())
	            .stream()
	            .findFirst()
	            .orElseThrow(() ->
	                    new ResourceNotFoundException(
	                            "Role not assigned to user"));

	    RoleMaster roleMaster = userRole.getRoleMaster();

	    String token = jwtService.generateToken(
	    		userRole
	    );

	    // 6. Response
	    Map<String, Object> data = new HashMap<>();

	    data.put("accessToken", token);
	    data.put("tokenType", "Bearer");
	    data.put("userAccountId",
	            userAccount.getUserAccountId());
	    data.put("username",
	            userAccount.getUsername());
	    data.put("email",
	            userAccount.getEmail());
	    data.put("roleName",
	            roleMaster.getRoleName());

	    return ResponseEntity.ok(
	            ApiResponseDto.builder()
	                    .success(true)
	                    .message("Login successful")
	                    .data(data)
	                    .build()
	    );
	}


}
