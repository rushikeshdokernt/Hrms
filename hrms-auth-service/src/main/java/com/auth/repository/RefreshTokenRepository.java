package com.auth.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.auth.entity.RefreshToken;
import com.auth.entity.UserAccounts;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID>{

	Optional<RefreshToken> findByToken(String token);

	List<RefreshToken> findByUserAccounts(UserAccounts user);

}
