package com.employee.external.tenant.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantConnectionConfigDto {
    private UUID   tenantId;
    private String tenantName;
    private String tenantCode;
    private String subdomain;
    private String dbHost;
    private Integer dbPort;
    private String dbName;
    private String dbUser;
    private String dbPasswordSecret;
    private String status;
    private String type;
    private String timezone;
    private String currency;
    private String locale;
}
