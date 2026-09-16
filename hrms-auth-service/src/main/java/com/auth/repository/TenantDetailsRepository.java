package com.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.auth.controller.TenantDetails;
import com.auth.entity.RoleMaster;

public interface TenantDetailsRepository extends JpaRepository<TenantDetails, UUID>{

	//Optional<TenantDetails> findFirstByOrderByIdAsc();

	Optional<TenantDetails> findFirstByOrderByTenantDetailIdAsc();

}
