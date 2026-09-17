package com.auth.multitenancy;

import java.sql.Connection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.auth.dto.response.TenantConnectionConfigDto;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TenantDataSourceManager {

    private final Map<String, HikariDataSource> dataSources = new ConcurrentHashMap<>();
    private final MultiTenantRoutingDataSource multiTenantRoutingDataSource;

    public TenantDataSourceManager(@Lazy MultiTenantRoutingDataSource multiTenantRoutingDataSource) {
        this.multiTenantRoutingDataSource = multiTenantRoutingDataSource;
    }

    /**
     * Retrieves an existing HikariDataSource connection pool or creates and warms up a new one.
     * Guaranteed to be created and warmed BEFORE the user logs in.
     */
    public synchronized HikariDataSource getOrCreateDataSource(TenantConnectionConfigDto tenantConfig) {
        if (tenantConfig == null || tenantConfig.getTenantCode() == null) {
            throw new IllegalArgumentException("Tenant configuration or tenant code cannot be null");
        }

        String tenantCode = tenantConfig.getTenantCode().toLowerCase().trim();

        // 1. Return existing active pool if present
        HikariDataSource existing = dataSources.get(tenantCode);
        if (existing != null && !existing.isClosed() && existing.isRunning()) {
            log.debug("Found active connection pool for tenant: {}", tenantCode);
            return existing;
        }

        log.info("Creating new HikariCP connection pool for tenant [{}] before login...", tenantCode);

        // 2. Build Hikari configuration
        String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s",
                tenantConfig.getDbHost(),
                tenantConfig.getDbPort() != null ? tenantConfig.getDbPort() : 5432,
                tenantConfig.getDbName());

        log.info("===> [TENANT CONNECTION POOL] Connecting tenant [{}] to JDBC URL: [{}]", tenantCode, jdbcUrl);

        HikariConfig config = new HikariConfig();
        config.setPoolName("HikariPool-Tenant-" + tenantCode);
        config.setDriverClassName("org.postgresql.Driver");
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(tenantConfig.getDbUser());
        config.setPassword(tenantConfig.getDbPasswordSecret());

        config.setSchema("rbac");

        // Pool sizing & timeout management
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(1);
        config.setIdleTimeout(300_000);        // 5 minutes
        config.setMaxLifetime(1_800_000);      // 30 minutes
        config.setConnectionTimeout(10_000);   // 10 seconds
        config.setValidationTimeout(3_000);    // 3 seconds
        config.setLeakDetectionThreshold(20_000); // 20 seconds

        // PostgreSQL specific optimizations
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        HikariDataSource dataSource = new HikariDataSource(config);

        // 3. Pre-login Warmup: eagerly acquire a connection to initialize the pool
        try (Connection connection = dataSource.getConnection()) {
            log.info("Connection pool successfully initialized and warmed for tenant [{}]. Valid: {}",
                    tenantCode, connection.isValid(2));
        } catch (Exception ex) {
            log.error("Failed to validate/warm connection pool for tenant [{}]: {}", tenantCode, ex.getMessage());
            dataSource.close();
            throw new RuntimeException("Database connection failure for tenant: " + tenantCode, ex);
        }

        // 4. Register in map & routing data source under tenantCode, subdomain, and tenantId (UUID)
        dataSources.put(tenantCode, dataSource);
        if (multiTenantRoutingDataSource != null) {
            multiTenantRoutingDataSource.addTenantDataSource(tenantCode, dataSource);
        }

        if (tenantConfig.getSubdomain() != null && !tenantConfig.getSubdomain().isBlank()) {
            String subKey = tenantConfig.getSubdomain().toLowerCase().trim();
            dataSources.put(subKey, dataSource);
            if (multiTenantRoutingDataSource != null) {
                multiTenantRoutingDataSource.addTenantDataSource(subKey, dataSource);
            }
        }

        if (tenantConfig.getTenantId() != null) {
            String idKey = tenantConfig.getTenantId().toString().toLowerCase().trim();
            dataSources.put(idKey, dataSource);
            if (multiTenantRoutingDataSource != null) {
                multiTenantRoutingDataSource.addTenantDataSource(idKey, dataSource);
            }
            log.info("Registered connection pool under tenantId: [{}]", idKey);
        }

        return dataSource;
    }

    public HikariDataSource getDataSource(String tenantCode) {
        if (tenantCode == null) return null;
        return dataSources.get(tenantCode.toLowerCase().trim());
    }

    public boolean isPoolReady(String tenantCode) {
        if (tenantCode == null) return false;
        HikariDataSource ds = dataSources.get(tenantCode.toLowerCase().trim());
        return ds != null && !ds.isClosed() && ds.isRunning();
    }

    public synchronized void closeTenantDataSource(String tenantCode) {
        if (tenantCode == null) return;
        HikariDataSource ds = dataSources.remove(tenantCode.toLowerCase().trim());
        if (ds != null && !ds.isClosed()) {
            log.info("Closing connection pool for tenant: {}", tenantCode);
            ds.close();
            if (multiTenantRoutingDataSource != null) {
                multiTenantRoutingDataSource.removeTenantDataSource(tenantCode);
            }
        }
    }

    @PreDestroy
    public void closeAllDataSources() {
        log.info("Shutting down all tenant connection pools...");
        dataSources.forEach((code, ds) -> {
            try {
                if (ds != null && !ds.isClosed()) {
                    ds.close();
                }
            } catch (Exception e) {
                log.warn("Error closing connection pool for tenant [{}]: {}", code, e.getMessage());
            }
        });
        dataSources.clear();
    }
}
