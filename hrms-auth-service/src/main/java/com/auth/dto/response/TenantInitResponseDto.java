package com.auth.dto.response;

import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantInitResponseDto {
    private UUID tenantId;
    private String tenantName;
    private String tenantCode;
    private String subdomain;
    private String status;
    private String formType;
    private JsonNode formFields;
    private boolean isConnectionPoolReady;
    private String message;
}
