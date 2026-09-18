package com.auth.serviceimpl;

import org.springframework.stereotype.Service;

import com.auth.dto.response.ApiResponseDto;
import com.auth.dto.response.TenantConnectionConfigDto;
import com.auth.dto.response.TenantInitResponseDto;
import com.auth.entity.DynamicFormStructure;
import com.auth.exception.BadRequestException;
import com.auth.external.tenant.util.TenantServiceUtil;
import com.auth.multitenancy.TenantContext;
import com.auth.multitenancy.TenantDataSourceManager;
import com.auth.repository.DynamicFormRepository;
import com.auth.service.TenantInitService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantInitServiceImpl implements TenantInitService {

    private final TenantServiceUtil tenantServiceUtil;
    private final TenantDataSourceManager tenantDataSourceManager;
    private final DynamicFormRepository dynamicFormRepository;
    private final ObjectMapper objectMapper;

    @Override
    public ApiResponseDto initializeTenantOnDomainHit(String subdomain, String domain , String headerTenant) {
        // 1. Identify tenant target key
        TenantConnectionConfigDto tenantConfig = null;
        System.out.println(subdomain+"----------------------------------------------"+domain);
        if (headerTenant != null && !headerTenant.isBlank()) {
        	
            tenantConfig = tenantServiceUtil.getTenantByCode(headerTenant.trim());
        } else if (subdomain != null && !subdomain.isBlank()) {
        	System.out.println("==============================llllll="+subdomain);
            tenantConfig = tenantServiceUtil.getTenantBySubdomain(subdomain.trim());
        } else if (domain != null && !domain.isBlank()) {
            tenantConfig = tenantServiceUtil.resolveTenantByDomain(domain.trim());
        } else {
            throw new BadRequestException("Domain, subdomain, or tenant code must be provided");
        }

        if (tenantConfig == null) {
            throw new BadRequestException("Unable to resolve tenant information");
        }

        log.info("Tenant resolved: [{}] ({}) - Initializing connection pool & loading dynamic form",
                tenantConfig.getTenantCode(), tenantConfig.getTenantName());

        // 2. Validate tenant status
        if ("SUSPENDED".equalsIgnoreCase(tenantConfig.getStatus())) {
            throw new BadRequestException("Tenant account is suspended. Please contact system administrator.");
        }

        // 3. CREATE & WARM UP CONNECTION POOL BEFORE LOGIN
        boolean poolReady = false;
        try {
            tenantDataSourceManager.getOrCreateDataSource(tenantConfig);
            poolReady = tenantDataSourceManager.isPoolReady(tenantConfig.getTenantCode());
            log.info("Connection pool pre-created and warmed up successfully for tenant [{}] before login: {}",
                    tenantConfig.getTenantCode(), poolReady);
        } catch (Exception ex) {
            log.error("Failed to pre-create connection pool for tenant [{}]: {}", tenantConfig.getTenantCode(), ex.getMessage());
            throw new RuntimeException("Failed to establish tenant database connection pool: " + ex.getMessage(), ex);
        }

        // 4. LOAD DYNAMIC LOGIN FORM FOR THE TENANT
        System.out.println("=================================================================");
        System.out.println("=================================================================");
        String formType = "LOGIN";
        JsonNode formFields;

        // TenantFilter returns null for raw IPv4 addresses (e.g. 172.20.1.62),
        // so TenantContext may not be set yet. We explicitly set it here so that
        // all repository queries below route to the correct tenant datasource
        // instead of falling back to the default app.properties datasource.
        boolean tenantContextSetByUs = false;
        if (TenantContext.getCurrentTenant() == null || TenantContext.getCurrentTenant().isBlank()) {
            TenantContext.setCurrentTenant(tenantConfig.getTenantCode());
            tenantContextSetByUs = true;
            log.info("TenantContext explicitly set to [{}] inside TenantInitService (IP-based access)", tenantConfig.getTenantCode());
        }

        try {
            DynamicFormStructure formStructure = dynamicFormRepository.findByFormType(formType);

            if (formStructure != null && formStructure.getFormFields() != null) {
                formFields = formStructure.getFormFields();
                formType = formStructure.getFormType();
            } else {
                // Fallback default form fields if none seeded in DB yet
                formFields = createDefaultLoginForm();
            }
        } finally {
            // Only clear if we set it — let TenantFilter manage its own context
            if (tenantContextSetByUs) {
                TenantContext.clear();
            }
        }
        

        // 5. Construct initialization response
        TenantInitResponseDto responseDto = TenantInitResponseDto.builder()
                .tenantId(tenantConfig.getTenantId())
                .tenantName(tenantConfig.getTenantName())
                .tenantCode(tenantConfig.getTenantCode())
                .subdomain(tenantConfig.getSubdomain())
                .status(tenantConfig.getStatus())
                .formType(formType)
                .formFields(formFields)
                .isConnectionPoolReady(poolReady)
                .message("Tenant connection pool created, warmed up, and dynamic login form loaded successfully")
                .build();

        return ApiResponseDto.builder()
                .success(true)
                .message("Tenant initialized successfully")
                .data(responseDto)
                .build();
    }

    private JsonNode createDefaultLoginForm() {
        ArrayNode fields = objectMapper.createArrayNode();

        ObjectNode emailField = objectMapper.createObjectNode();
        emailField.put("name", "email");
        emailField.put("label", "Email Address");
        emailField.put("type", "email");
        emailField.put("placeholder", "Enter your corporate email====================");
        emailField.put("required", true);
        fields.add(emailField);

        ObjectNode passwordField = objectMapper.createObjectNode();
        passwordField.put("name", "password");
        passwordField.put("label", "Password");
        passwordField.put("type", "password");
        passwordField.put("placeholder", "Enter your password");
        passwordField.put("required", true);
        fields.add(passwordField);

        return fields;
    }
}
