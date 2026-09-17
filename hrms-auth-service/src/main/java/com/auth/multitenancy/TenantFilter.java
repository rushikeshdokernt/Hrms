package com.auth.multitenancy;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Pattern;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth.dto.response.TenantConnectionConfigDto;
import com.auth.external.tenant.util.TenantServiceUtil;
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

    public static final String HEADER_TENANT_ID = "X-Tenant-Id";
    public static final String HEADER_TENANT_CODE = "X-Tenant-Code";
    public static final String HEADER_TENANT_SUBDOMAIN = "X-Tenant-Subdomain";
    public static final String HEADER_TENANT_DOMAIN = "X-Tenant-Domain";

    private static final Pattern UUID_PATTERN =
            Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

    private static final Pattern IPV4_PATTERN =
            Pattern.compile("^\\d{1,3}(\\.\\d{1,3}){3}$");

    private final TenantDataSourceManager tenantDataSourceManager;
    private final TenantServiceUtil tenantServiceUtil;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String tenant = resolveTenant(request);

        if (tenant != null && !tenant.isBlank()) {
            log.debug("Tenant resolved for request [{} {}]: {}", request.getMethod(), request.getRequestURI(), tenant);
            
            // Ensure connection pool exists in MultiTenantRoutingDataSource for this tenant
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
                    config = tenantServiceUtil.getTenantById(tenant);
                } else {
                    config = tenantServiceUtil.getTenantBySubdomain(tenant);
                }
                if (config != null) {
                    tenantDataSourceManager.getOrCreateDataSource(config);
                    log.info("Dynamically ensured connection pool for tenant [{}] in TenantFilter", tenant);
                }
            } catch (Exception ex) {
                log.warn("Could not ensure connection pool for tenant [{}]: {}", tenant, ex.getMessage());
            }
        }
    }

    private String resolveTenant(HttpServletRequest request) {
        // 1. Check X-Tenant-Id header
        String tenant = request.getHeader(HEADER_TENANT_ID);
        if (tenant != null && !tenant.isBlank()) {
            return tenant.trim();
        }

        // 2. Check X-Tenant-Subdomain or X-Tenant-Code header
        tenant = request.getHeader(HEADER_TENANT_SUBDOMAIN);
        if (tenant != null && !tenant.isBlank()) {
            return tenant.trim();
        }

        tenant = request.getHeader(HEADER_TENANT_CODE);
        if (tenant != null && !tenant.isBlank()) {
            return tenant.trim();
        }

        // 3. Check query parameters
        tenant = request.getParameter("tenantId");
        if (tenant != null && !tenant.isBlank()) {
            return tenant.trim();
        }

        tenant = request.getParameter("subdomain");
        if (tenant != null && !tenant.isBlank()) {
            return tenant.trim();
        }

        // 4. Extract tenantId from JWT Bearer token (for all authenticated APIs)
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7).trim();
            String jwtTenantId = extractTenantIdFromJwt(token);
            if (jwtTenantId != null && !jwtTenantId.isBlank()) {
                return jwtTenantId;
            }
        }

        // 5. Extract host from X-Forwarded-Host, Host, Origin, or Referer
        String hostCandidate = request.getHeader("X-Forwarded-Host");
        if (hostCandidate == null || hostCandidate.isBlank()) {
            hostCandidate = request.getHeader("Host");
        }
        if (hostCandidate == null || hostCandidate.isBlank()) {
            hostCandidate = request.getHeader("Origin");
        }
        if (hostCandidate == null || hostCandidate.isBlank()) {
            hostCandidate = request.getHeader("Referer");
        }

        if (hostCandidate != null && !hostCandidate.isBlank()) {
            String cleanHost = hostCandidate.toLowerCase().trim();
            if (cleanHost.contains("://")) {
                cleanHost = cleanHost.substring(cleanHost.indexOf("://") + 3);
            }
            if (cleanHost.contains("/")) {
                cleanHost = cleanHost.substring(0, cleanHost.indexOf("/"));
            }
            if (cleanHost.contains(":")) {
                cleanHost = cleanHost.substring(0, cleanHost.indexOf(":"));
            }

            // If localhost
            if (cleanHost.equalsIgnoreCase("localhost") || cleanHost.equals("127.0.0.1")) {
                return "localhost";
            }
            // If raw IP address (like 172.20.1.57), do NOT treat "172" as a tenant!
            if (IPV4_PATTERN.matcher(cleanHost).matches()) {
                return null;
            }
            // If domain with dots (like hnt.ai or acme.hrms.com)
            if (cleanHost.contains(".")) {
                return cleanHost.substring(0, cleanHost.indexOf('.'));
            } else {
                return cleanHost;
            }
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
