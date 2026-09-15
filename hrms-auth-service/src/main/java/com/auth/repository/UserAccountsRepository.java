package com.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.auth.entity.RoleMaster;
import com.auth.entity.UserAccounts;

public interface UserAccountsRepository extends JpaRepository<UserAccounts, UUID>{

	boolean existsByEmail(String email);

	//Optional<UserAccounts> findByUsername(String username);

	Optional<UserAccounts> findByEmail(String username);

	//Optional<UserAccounts> findByContact(String contact);

}
