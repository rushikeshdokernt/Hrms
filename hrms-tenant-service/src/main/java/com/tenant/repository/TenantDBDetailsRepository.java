package com.tenant.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tenant.entity.TenantsDBDetails;

@Repository
public interface TenantDBDetailsRepository extends JpaRepository<TenantsDBDetails, UUID> {
    Optional<TenantsDBDetails> findBySubdomain(String subdomain);
    Optional<TenantsDBDetails> findByTenantCode(String tenantCode);
    Optional<TenantsDBDetails> findByDomainUrl(String domainUrl);
}
