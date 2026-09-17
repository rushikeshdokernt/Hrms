package com.auth.external.tenant.util;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.auth.dto.response.TenantConnectionConfigDto;
import com.auth.external.tenant.client.TenantFeignClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class TenantServiceUtil {

    private final TenantFeignClient tenantFeignClient;

    // In-memory cache to prevent repeated remote Feign calls
    private final Map<String, CachedTenant> cache = new ConcurrentHashMap<>();
    private static final long CACHE_TTL_MILLIS = 10 * 60 * 1000; // 10 minutes

    private static class CachedTenant {
        final TenantConnectionConfigDto config;
        final long cachedAt;

        CachedTenant(TenantConnectionConfigDto config) {
            this.config = config;
            this.cachedAt = System.currentTimeMillis();
        }

        boolean isExpired() {
            return (System.currentTimeMillis() - cachedAt) > CACHE_TTL_MILLIS;
        }
    }

    private void cacheTenant(TenantConnectionConfigDto config) {
        if (config == null) return;
        CachedTenant entry = new CachedTenant(config);
        if (config.getTenantCode() != null) {
            cache.put("code:" + config.getTenantCode().toLowerCase().trim(), entry);
        }
        if (config.getSubdomain() != null) {
            cache.put("subdomain:" + config.getSubdomain().toLowerCase().trim(), entry);
        }
        if (config.getTenantId() != null) {
            cache.put("id:" + config.getTenantId().toString().toLowerCase().trim(), entry);
        }
    }

    public TenantConnectionConfigDto resolveTenantByDomain(String domain) {
        if (domain == null || domain.isBlank()) return null;
        String cacheKey = "domain:" + domain.toLowerCase().trim();
        CachedTenant cached = cache.get(cacheKey);
        if (cached != null && !cached.isExpired()) {
            log.debug("Tenant cache hit for domain [{}]", domain);
            return cached.config;
        }

        log.info("Calling tenant-service via Feign: resolveTenantByDomain [{}]", domain);
        try {
            ResponseEntity<TenantConnectionConfigDto> response = tenantFeignClient.resolveTenantByDomain(domain.trim());
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                TenantConnectionConfigDto config = response.getBody();
                cache.put(cacheKey, new CachedTenant(config));
                cacheTenant(config);
                return config;
            }
        } catch (Exception ex) {
            log.error("Feign call failed for resolveTenantByDomain [{}]: {}", domain, ex.getMessage());
            throw new RuntimeException("Unable to resolve tenant for domain: " + domain, ex);
        }
        return null;
    }

    public TenantConnectionConfigDto getTenantByCode(String tenantCode) {
        if (tenantCode == null || tenantCode.isBlank()) return null;
        String cacheKey = "code:" + tenantCode.toLowerCase().trim();
        CachedTenant cached = cache.get(cacheKey);
        if (cached != null && !cached.isExpired()) {
            return cached.config;
        }

        log.info("Calling tenant-service via Feign: getTenantConfigByCode [{}]", tenantCode);
        try {
            ResponseEntity<TenantConnectionConfigDto> response = tenantFeignClient.getTenantConfigByCode(tenantCode.trim());
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                TenantConnectionConfigDto config = response.getBody();
                cacheTenant(config);
                return config;
            }
        } catch (Exception ex) {
            log.error("Feign call failed for getTenantConfigByCode [{}]: {}", tenantCode, ex.getMessage());
            throw new RuntimeException("Unable to fetch tenant for code: " + tenantCode, ex);
        }
        return null;
    }

    public TenantConnectionConfigDto getTenantBySubdomain(String subdomain) {
        if (subdomain == null || subdomain.isBlank()) return null;
        String cacheKey = "subdomain:" + subdomain.toLowerCase().trim();
        CachedTenant cached = cache.get(cacheKey);
        if (cached != null && !cached.isExpired()) {
            return cached.config;
        }

        log.info("Calling tenant-service via Feign: getTenantConfigBySubdomain [{}]", subdomain);
        try {
            ResponseEntity<TenantConnectionConfigDto> response = tenantFeignClient.getTenantConfigBySubdomain(subdomain.trim());
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            	
            	System.out.println("---------===========---------->"+response.getBody());
                TenantConnectionConfigDto config = response.getBody();
                cacheTenant(config);
                return config;
            }
        } catch (Exception ex) {
            log.error("Feign call failed for getTenantConfigBySubdomain [{}]: {}", subdomain, ex.getMessage());
            throw new RuntimeException("Unable to fetch tenant for subdomain: " + subdomain, ex);
        }
        return null;
    }

    public TenantConnectionConfigDto getTenantById(String tenantId) {
        if (tenantId == null || tenantId.isBlank()) return null;
        String cacheKey = "id:" + tenantId.toLowerCase().trim();
        CachedTenant cached = cache.get(cacheKey);
        if (cached != null && !cached.isExpired()) {
            return cached.config;
        }

        log.info("Calling tenant-service via Feign: getTenantConfigById [{}]", tenantId);
        try {
            UUID uuid = UUID.fromString(tenantId.trim());
            ResponseEntity<TenantConnectionConfigDto> response = tenantFeignClient.getTenantConfigById(uuid);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                TenantConnectionConfigDto config = response.getBody();
                cacheTenant(config);
                return config;
            }
        } catch (Exception ex) {
            log.error("Feign call failed for getTenantConfigById [{}]: {}", tenantId, ex.getMessage());
            throw new RuntimeException("Unable to fetch tenant for id: " + tenantId, ex);
        }
        return null;
    }

    public TenantConnectionConfigDto getTenantById(UUID tenantId) {
        return tenantId != null ? getTenantById(tenantId.toString()) : null;
    }
}
