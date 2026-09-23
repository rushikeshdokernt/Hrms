package com.employee.multitenancy;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Pattern;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.employee.external.tenant.dto.TenantConnectionConfigDto;
import com.employee.external.tenant.util.TenantServiceUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
@Slf4j
@RequiredArgsConstructor
public class TenantFilter extends OncePerRequestFilter {

    public static final String HEADER_TENANT_ID        = "X-Tenant-Id";
    public static final String HEADER_TENANT_CODE      = "X-Tenant-Code";
    public static final String HEADER_TENANT_SUBDOMAIN = "X-Tenant-Subdomain";
    public static final String HEADER_TENANT_DOMAIN    = "X-Tenant-Domain";

    private static final Pattern UUID_PATTERN =
            Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

    private static final Pattern IPV4_PATTERN =
            Pattern.compile("^\\d{1,3}(\\.\\d{1,3}){3}$");

    private final TenantDataSourceManager tenantDataSourceManager;
    private final TenantServiceUtil       tenantServiceUtil;
    private final ObjectMapper            objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String tenant = resolveTenant(request);

        if (tenant != null && !tenant.isBlank()) {
            log.debug("Tenant resolved for request [{} {}]: {}",
                    request.getMethod(), request.getRequestURI(), tenant);

            // Ensure HikariCP pool exists for this tenant before the request proceeds
            ensureTenantPoolActive(tenant);

            TenantContext.setCurrentTenant(tenant);
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }

    private void ensureTenantPoolActive(String tenant) {
        if (!tenantDataSourceManager.isPoolReady(tenant)) {
            try {
                TenantConnectionConfigDto config = null;

                if (UUID_PATTERN.matcher(tenant).matches()) {
                    // Resolved from JWT tenantId claim
                    config = tenantServiceUtil.getTenantById(tenant);
                } else {
                    // Try tenant_code first — this is what IP resolution returns
                    // (resolveTenantByDomain returns config.getTenantCode(), not subdomain)
                    try {
                        config = tenantServiceUtil.getTenantByCode(tenant);
                    } catch (Exception ignored) {
                        log.debug("getTenantByCode lookup missed for [{}], trying subdomain", tenant);
                    }
                    // Fallback: try subdomain — used when host header has acme.hrms.com → "acme"
                    if (config == null) {
                        config = tenantServiceUtil.getTenantBySubdomain(tenant);
                    }
                }

                if (config != null) {
                    tenantDataSourceManager.getOrCreateDataSource(config);
                    log.info("Dynamically ensured connection pool for tenant [{}] in TenantFilter", tenant);
                } else {
                    log.warn("No tenant config found for key [{}] — skipping pool init", tenant);
                }
            } catch (Exception ex) {
                log.warn("Could not ensure connection pool for tenant [{}]: {}", tenant, ex.getMessage());
            }
        }
    }

    private String resolveTenant(HttpServletRequest request) {

        // 1. X-Tenant-Id header
        String tenant = request.getHeader(HEADER_TENANT_ID);
        if (tenant != null && !tenant.isBlank()) return tenant.trim();

        // 2. X-Tenant-Subdomain or X-Tenant-Code header
        tenant = request.getHeader(HEADER_TENANT_SUBDOMAIN);
        if (tenant != null && !tenant.isBlank()) return tenant.trim();

        tenant = request.getHeader(HEADER_TENANT_CODE);
        if (tenant != null && !tenant.isBlank()) return tenant.trim();

        // 3. Query parameters
        tenant = request.getParameter("tenantId");
        if (tenant != null && !tenant.isBlank()) return tenant.trim();

        tenant = request.getParameter("subdomain");
        if (tenant != null && !tenant.isBlank()) return tenant.trim();

        // 4. JWT Bearer token — extract tenantId claim
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwtTenantId = extractTenantIdFromJwt(authHeader.substring(7).trim());
            if (jwtTenantId != null && !jwtTenantId.isBlank()) return jwtTenantId;
        }

        // 5. Host / X-Forwarded-Host / Origin / Referer
        String hostCandidate = request.getHeader("X-Forwarded-Host");
        if (hostCandidate == null || hostCandidate.isBlank()) hostCandidate = request.getHeader("Host");
        if (hostCandidate == null || hostCandidate.isBlank()) hostCandidate = request.getHeader("Origin");
        if (hostCandidate == null || hostCandidate.isBlank()) hostCandidate = request.getHeader("Referer");

        if (hostCandidate != null && !hostCandidate.isBlank()) {
            String cleanHost = hostCandidate.toLowerCase().trim();
            if (cleanHost.contains("://"))  cleanHost = cleanHost.substring(cleanHost.indexOf("://") + 3);
            if (cleanHost.contains("/"))    cleanHost = cleanHost.substring(0, cleanHost.indexOf("/"));
            if (cleanHost.contains(":"))    cleanHost = cleanHost.substring(0, cleanHost.indexOf(":"));

            // localhost
            if (cleanHost.equalsIgnoreCase("localhost") || cleanHost.equals("127.0.0.1")) {
                return "localhost";
            }

            // Raw IPv4 (e.g. 172.20.1.62) → resolve via tenant-service
            if (IPV4_PATTERN.matcher(cleanHost).matches()) {
                try {
                    TenantConnectionConfigDto config = tenantServiceUtil.resolveTenantByDomain(cleanHost);
                    if (config != null && config.getTenantCode() != null && !config.getTenantCode().isBlank()) {
                        log.debug("Resolved tenant [{}] from IP [{}]", config.getTenantCode(), cleanHost);
                        return config.getTenantCode().toLowerCase().trim();
                    }
                } catch (Exception ex) {
                    log.warn("Could not resolve tenant for IP [{}]: {}", cleanHost, ex.getMessage());
                }
                return null;
            }

            // Subdomain (e.g. acme.hrms.com → "acme")
            if (cleanHost.contains(".")) {
                return cleanHost.substring(0, cleanHost.indexOf('.'));
            }
            return cleanHost;
        }

        return null;
    }

    private String extractTenantIdFromJwt(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length >= 2) {
                byte[] decoded = Base64.getUrlDecoder().decode(parts[1]);
                String payload = new String(decoded, StandardCharsets.UTF_8);
                JsonNode claims = objectMapper.readTree(payload);
                if (claims.hasNonNull("tenantId")) {
                    return claims.get("tenantId").asText();
                }
            }
        } catch (Exception e) {
            log.debug("Unable to parse tenantId from JWT: {}", e.getMessage());
        }
        return null;
    }
}
