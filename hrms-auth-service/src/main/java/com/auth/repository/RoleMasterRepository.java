package com.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.auth.entity.RoleMaster;

public interface RoleMasterRepository extends JpaRepository<RoleMaster, UUID>{

	boolean existsByRoleName(String string);

	Optional<RoleMaster> findByRoleName(String string);

}
