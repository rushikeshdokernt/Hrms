package com.auth.external.tenant.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.auth.dto.response.TenantConnectionConfigDto;

@FeignClient(name = "tenant-service", path = "/api/v1/tenant")
public interface TenantFeignClient {

    @GetMapping("/internal/resolve")
    ResponseEntity<TenantConnectionConfigDto> resolveTenantByDomain(@RequestParam("domain") String domain);

    @GetMapping("/internal/config/{tenantCode}")
    ResponseEntity<TenantConnectionConfigDto> getTenantConfigByCode(@PathVariable("tenantCode") String tenantCode);

    @GetMapping("/internal/subdomain/{subdomain}")
    ResponseEntity<TenantConnectionConfigDto> getTenantConfigBySubdomain(@PathVariable("subdomain") String subdomain);

    @GetMapping("/internal/config/id/{tenantId}")
    ResponseEntity<TenantConnectionConfigDto> getTenantConfigById(@PathVariable("tenantId") UUID tenantId);
}
