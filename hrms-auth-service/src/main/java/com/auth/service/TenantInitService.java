package com.auth.service;

import com.auth.dto.response.ApiResponseDto;

public interface TenantInitService {
    ApiResponseDto initializeTenantOnDomainHit(String domain, String subdomain, String headerTenant);
}
