package com.tenant.serviceimpl;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.tenant.dto.response.TenantConnectionConfigDto;
import com.tenant.entity.TenantsDBDetails;
import com.tenant.repository.TenantDBDetailsRepository;
import com.tenant.service.TenantService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantServiceImpl implements TenantService {

    private final TenantDBDetailsRepository tenantDBDetailsRepository;

    @Override
    public TenantConnectionConfigDto getTenantConfigBySubdomain(String subdomain) {
    	TenantsDBDetails tenant = tenantDBDetailsRepository.findBySubdomain(subdomain)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tenant not found with subdomain: " + subdomain));
        return mapToDto(tenant);
    }

    @Override
    public TenantConnectionConfigDto getTenantConfigByCode(String tenantCode) {
    	TenantsDBDetails tenant = tenantDBDetailsRepository.findByTenantCode(tenantCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tenant not found with code: " + tenantCode));
        return mapToDto(tenant);
    }

    @Override
    public TenantConnectionConfigDto getTenantConfigById(java.util.UUID tenantId) {
    	TenantsDBDetails tenant = tenantDBDetailsRepository.findById(tenantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tenant not found with ID: " + tenantId));
        return mapToDto(tenant);
    }

    @Override
    public TenantConnectionConfigDto resolveTenantByDomain(String domain) {
        if (domain == null || domain.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Domain cannot be empty");
        }

        // Clean domain (remove protocol and port if present)
        String cleaned = domain.toLowerCase().trim();
        if (cleaned.contains("://")) {
            cleaned = cleaned.substring(cleaned.indexOf("://") + 3);
        }
        if (cleaned.contains("/")) {
            cleaned = cleaned.substring(0, cleaned.indexOf("/"));
        }
        if (cleaned.contains(":")) {
            cleaned = cleaned.substring(0, cleaned.indexOf(":"));
        }

        // If domain is "acme.hrms.com", subdomain is "acme"
        final String effectiveCleaned = cleaned;
        final String effectiveSubdomain = effectiveCleaned.contains(".") ?
                effectiveCleaned.substring(0, effectiveCleaned.indexOf(".")) : effectiveCleaned;

        log.info("Resolving tenant for domain: {}, extracted subdomain/code: {}", domain, effectiveSubdomain);

        // Try lookup by subdomain first, then tenantCode
        return tenantDBDetailsRepository.findBySubdomain(effectiveSubdomain)
                .or(() -> tenantDBDetailsRepository.findByTenantCode(effectiveSubdomain))
                .or(() -> tenantDBDetailsRepository.findBySubdomain(effectiveCleaned))
                .map(this::mapToDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tenant not found for domain: " + domain));
    }

    private TenantConnectionConfigDto mapToDto(TenantsDBDetails tenant) {
        return TenantConnectionConfigDto.builder()
                .tenantId(tenant.getTenantId())
                .tenantCode(tenant.getTenantCode())
                .subdomain(tenant.getSubdomain())
                .dbHost(tenant.getDbHost())
                .dbPort(tenant.getDbPort())
                .dbName(tenant.getDbName())
                .dbUser(tenant.getDbUser())
                .dbPasswordSecret(tenant.getDbPasswordSecret())
                .status(tenant.getStatus())
                .type(tenant.getType())
                .timezone(tenant.getTimezone())
                .locale(tenant.getLocale())
                .build();
    }
}
