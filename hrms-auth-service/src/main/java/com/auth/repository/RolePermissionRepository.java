package com.auth.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.auth.entity.RolePermission;

public interface RolePermissionRepository extends JpaRepository<RolePermission, UUID>{
	
	List<RolePermission> findByRoleRoleId(UUID roleId);

}
