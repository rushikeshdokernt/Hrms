package com.tenant.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

import com.tenant.enums.TenantStatus;
import com.tenant.enums.TenantType;

@Entity
@Table(name = "tenants_db_details", schema = "tenant")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TenantsDBDetails extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "tenant_id", nullable = false, updatable = false)
    private UUID tenantId;

    @Column(name = "tenant_code", nullable = false, unique = true, length = 50)
    private String tenantCode;

    @Column(name = "subdomain", nullable = false, unique = true, length = 100)
    private String subdomain;
    
    @Column(name = "domain_url", nullable = false, unique = true, length = 100)
    private String domainUrl;

    @Column(name = "db_host", nullable = false, length = 255)
    private String dbHost;

    @Column(name = "db_port", nullable = false)
    @Builder.Default
    private Integer dbPort = 5432;

    @Column(name = "db_name", nullable = false, length = 100)
    private String dbName;

    @Column(name = "db_user", nullable = false, length = 100)
    private String dbUser;

    @Column(name = "db_password_secret", nullable = false, length = 500)
    private String dbPasswordSecret;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private TenantStatus status = TenantStatus.INACTIVE;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tenant_type", nullable = false, length = 20)
    @Builder.Default
    private TenantType type = TenantType.CLIENT;

    @Column(name = "timezone", length = 100)
    @Builder.Default
    private String timezone = "Asia/Kolkata";

    @Column(name = "locale", length = 20)
    @Builder.Default
    private String locale = "en-IN";
}
