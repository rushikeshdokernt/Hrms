package com.tenant.dto.request;

import com.tenant.enums.TenantStatus;
import com.tenant.enums.TenantType;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AddTenantRequestDto {

    // ── TenantsDBDetails fields ──────────────────────────────────────────────

    @NotBlank(message = "Tenant code is required")
    @Size(max = 50, message = "Tenant code must not exceed 50 characters")
    private String tenantCode;

    @NotBlank(message = "Subdomain is required")
    @Size(max = 100, message = "Subdomain must not exceed 100 characters")
    private String subdomain;

    @NotBlank(message = "Domain URL is required")
    @Size(max = 100, message = "Domain URL must not exceed 100 characters")
    private String domainUrl;

    @NotBlank(message = "DB host is required")
    private String dbHost;

    private Integer dbPort;

    @NotBlank(message = "DB name is required")
    private String dbName;

    @NotBlank(message = "DB user is required")
    private String dbUser;

    @NotBlank(message = "DB password is required")
    private String dbPasswordSecret;

    private TenantStatus status;

    private TenantType type = TenantType.CLIENT;

    @Size(max = 100)
    private String timezone;

    @Size(max = 20)
    private String locale;

    // ── TenantProfile fields ─────────────────────────────────────────────────

    @NotBlank(message = "Tenant name is required")
    @Size(max = 200, message = "Tenant name must not exceed 200 characters")
    private String tenantName;

    private Integer tenantSize;

    @Size(max = 15)
    private String gstin;

    @Size(max = 10)
    private String pan;

    @Size(max = 255)
    private String address;

    @Size(max = 10)
    private String pincode;

    @Size(max = 5)
    private String countryCode;

    @Size(max = 10)
    private String currency;

    @Size(max = 255)
    private String contactName;

    @Email(message = "Invalid contact email")
    @Size(max = 255)
    private String contactEmail;

    @Size(max = 30)
    private String contactPhone;
}
