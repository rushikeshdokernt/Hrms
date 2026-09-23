package com.employee.multitenancy;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class TenantContext {

    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();

    private TenantContext() {
    }

    public static void setCurrentTenant(String tenantCode) {
        log.debug("Setting TenantContext to: {}", tenantCode);
        CURRENT_TENANT.set(tenantCode);
    }

    public static String getCurrentTenant() {
        return CURRENT_TENANT.get();
    }

    public static void clear() {
        log.debug("Clearing TenantContext: {}", CURRENT_TENANT.get());
        CURRENT_TENANT.remove();
    }
}
