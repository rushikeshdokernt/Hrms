package com.auth.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.auth.entity.RoleMaster;
import com.auth.entity.UserRole;

public interface UserRoleRepository extends JpaRepository<UserRole, UUID>{

	List<UserRole> findByUserAccountUserAccountId(UUID userAccountId);

	//Optional<UserRole> findByUserAccountUserAccountId(UUID userAccountId);

}
