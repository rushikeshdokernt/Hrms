package com.tenant.service;

import com.tenant.dto.TenantConnectionConfigDto;

public interface TenantService {
    TenantConnectionConfigDto getTenantConfigBySubdomain(String subdomain);
    TenantConnectionConfigDto getTenantConfigByCode(String tenantCode);
    TenantConnectionConfigDto getTenantConfigById(java.util.UUID tenantId);
    TenantConnectionConfigDto resolveTenantByDomain(String domain);
}
