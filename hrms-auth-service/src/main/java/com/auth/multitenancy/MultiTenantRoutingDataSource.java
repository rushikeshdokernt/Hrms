package com.auth.multitenancy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MultiTenantRoutingDataSource extends AbstractRoutingDataSource {

    private final Map<Object, Object> targetDataSources = new ConcurrentHashMap<>();

    public MultiTenantRoutingDataSource(DataSource defaultTargetDataSource) {
        setDefaultTargetDataSource(defaultTargetDataSource);
        targetDataSources.put("default", defaultTargetDataSource);
        setTargetDataSources(targetDataSources);
    }

    @Override
    protected Object determineCurrentLookupKey() {
        String currentTenant = TenantContext.getCurrentTenant();
        log.debug("MultiTenantRoutingDataSource lookup key: {}", currentTenant);
        return currentTenant != null ? currentTenant.toLowerCase().trim() : "default";
    }

    public synchronized void addTenantDataSource(String tenantCode, DataSource dataSource) {
        String key = tenantCode.toLowerCase().trim();
        targetDataSources.put(key, dataSource);
        setTargetDataSources(new ConcurrentHashMap<>(targetDataSources));
        afterPropertiesSet();
        log.info("Registered tenant DataSource for key: [{}] in MultiTenantRoutingDataSource", key);
    }

    public synchronized void removeTenantDataSource(String tenantCode) {
        String key = tenantCode.toLowerCase().trim();
        targetDataSources.remove(key);
        setTargetDataSources(new ConcurrentHashMap<>(targetDataSources));
        afterPropertiesSet();
        log.info("Removed tenant DataSource for key: [{}] from MultiTenantRoutingDataSource", key);
    }
}
