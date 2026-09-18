package com.tenant.service;

import org.springframework.http.ResponseEntity;

import com.tenant.dto.request.AddTenantRequestDto;
import com.tenant.dto.response.ApiResponseDto;

public interface TenantManageService {

	ResponseEntity<ApiResponseDto> createTenant(AddTenantRequestDto request);
}
