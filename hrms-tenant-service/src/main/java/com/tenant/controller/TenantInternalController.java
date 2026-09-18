package com.tenant.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tenant.dto.response.TenantConnectionConfigDto;
import com.tenant.service.TenantService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/tenant")
@RequiredArgsConstructor
public class TenantInternalController {

    private final TenantService tenantService;

    @GetMapping("/internal/resolve")
    public ResponseEntity<TenantConnectionConfigDto> resolveTenant(@RequestParam("domain") String domain) {
        return ResponseEntity.ok(tenantService.resolveTenantByDomain(domain));
    }

    @GetMapping("/internal/config/{tenantCode}")
    public ResponseEntity<TenantConnectionConfigDto> getTenantConfigByCode(@PathVariable("tenantCode") String tenantCode) {
        return ResponseEntity.ok(tenantService.getTenantConfigByCode(tenantCode));
    }

    @GetMapping("/internal/subdomain/{subdomain}")
    public ResponseEntity<TenantConnectionConfigDto> getTenantConfigBySubdomain(@PathVariable("subdomain") String subdomain) {
        return ResponseEntity.ok(tenantService.getTenantConfigBySubdomain(subdomain));
    }

    @GetMapping("/internal/config/id/{tenantId}")
    public ResponseEntity<TenantConnectionConfigDto> getTenantConfigById(@PathVariable("tenantId") java.util.UUID tenantId) {
        return ResponseEntity.ok(tenantService.getTenantConfigById(tenantId));
    }
}
