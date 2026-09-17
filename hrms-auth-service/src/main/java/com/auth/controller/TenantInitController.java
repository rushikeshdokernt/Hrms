package com.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.auth.dto.response.ApiResponseDto;
import com.auth.service.TenantInitService;

import static com.auth.constant.ApiConstants.AUTH;
import static com.auth.constant.ApiConstants.TENANT_INIT;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(AUTH)
@RequiredArgsConstructor
@Slf4j
public class TenantInitController {

    private final TenantInitService tenantInitService;

    /**
     * Automatically extracts domain / subdomain from the request URL headers
     * (Host, Origin, Referer, X-Forwarded-Host, or Gateway headers) without requiring query parameters.
     * Example:
     * - http://localhost:8080/api/v1/auth/tenant/init -> resolves 'localhost'
     * - https://hnt.ai/api/v1/auth/tenant/init       -> resolves 'hnt'
     * - https://acme.hrms.com/...                    -> resolves 'acme'
     */
    @GetMapping(TENANT_INIT)
    public ResponseEntity<ApiResponseDto> initTenant(
            @RequestParam(value = "domain", required = false) String domainParam,
            @RequestParam(value = "subdomain", required = false) String subdomainParam,
            @RequestHeader(value = "X-Tenant-Subdomain", required = false) String headerSubdomain,
            @RequestHeader(value = "X-Tenant-Code", required = false) String headerCode,
            @RequestHeader(value = "X-Tenant-Domain", required = false) String headerDomain,
            @RequestHeader(value = "X-Forwarded-Host", required = false) String forwardedHost,
            @RequestHeader(value = "Host", required = false) String hostHeader,
            @RequestHeader(value = "Origin", required = false) String originHeader,
            @RequestHeader(value = "Referer", required = false) String refererHeader) {
    	
    	System.out.println("===================================");

        // 1. Determine effective subdomain or code if already provided by gateway
        String effectiveSubdomain = (subdomainParam != null && !subdomainParam.isBlank()) ? subdomainParam : headerSubdomain;

        // 2. Extract host from available sources if subdomain is not yet set
        String candidateHost = (domainParam != null && !domainParam.isBlank()) ? domainParam :
                (headerDomain != null && !headerDomain.isBlank()) ? headerDomain :
                (forwardedHost != null && !forwardedHost.isBlank()) ? forwardedHost :
                (hostHeader != null && !hostHeader.isBlank()) ? hostHeader :
                (originHeader != null && !originHeader.isBlank()) ? originHeader : refererHeader;

        log.info("Tenant init hit - Candidate host: [{}], headerSubdomain: [{}]", candidateHost, effectiveSubdomain);
        System.out.println("====================>"+headerCode);
        ApiResponseDto response = tenantInitService.initializeTenantOnDomainHit(
                candidateHost, effectiveSubdomain, headerCode);

        return ResponseEntity.ok(response);
    }
}
