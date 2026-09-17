package com.tenant.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tenant.entity.Tenants;

@Repository
public interface TenantRepository extends JpaRepository<Tenants, UUID> {
    Optional<Tenants> findBySubdomain(String subdomain);
    Optional<Tenants> findByTenantCode(String tenantCode);
}
