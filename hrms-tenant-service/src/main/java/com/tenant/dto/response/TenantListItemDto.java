package com.tenant.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.tenant.enums.TenantStatus;
import com.tenant.enums.TenantType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TenantListItemDto {

    // ── Identity ─────────────────────────────────────────────────────────────
    private UUID   tenantId;
    private String tenantCode;
    private String subdomain;
    private String domainUrl;

    // ── Profile ───────────────────────────────────────────────────────────────
    private UUID    tenantProfileId;
    private String  tenantName;
    private Integer tenantSize;
    private String  gstin;
    private String  pan;
    private String  address;
    private String  pincode;
    private String  countryCode;
    private String  currency;
    private String  contactName;
    private String  contactEmail;
    private String  contactPhone;

    // ── DB Config (no password exposed) ──────────────────────────────────────
    private String  dbHost;
    private Integer dbPort;
    private String  dbName;
    private String  dbUser;

    // ── Status & Metadata ─────────────────────────────────────────────────────
    private TenantStatus status;
    private TenantType   type;
    private String       timezone;
    private String       locale;
    private OffsetDateTime createdDate;
    private OffsetDateTime updatedDate;
}
