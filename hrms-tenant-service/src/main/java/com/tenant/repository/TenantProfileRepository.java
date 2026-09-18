package com.tenant.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tenant.entity.TenantProfile;

@Repository
public interface TenantProfileRepository extends JpaRepository<TenantProfile, UUID> {
}
