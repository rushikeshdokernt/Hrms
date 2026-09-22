package com.tenant.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tenant.entity.TenantProfile;

@Repository
public interface TenantProfileRepository extends JpaRepository<TenantProfile, UUID> {

    /**
     * Fetches a page of tenant profiles with their DB details in a single
     * JOIN FETCH query, preventing N+1 on the LAZY @OneToOne relation.
     */
    @Query("SELECT tp FROM TenantProfile tp JOIN FETCH tp.tenantsDBDetails")
    Page<TenantProfile> findAllWithDbDetails(Pageable pageable);
}
