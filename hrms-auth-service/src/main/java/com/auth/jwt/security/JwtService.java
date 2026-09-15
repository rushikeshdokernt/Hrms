package com.auth.jwt.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.auth.entity.RoleMaster;
import com.auth.entity.UserAccounts;
import com.auth.entity.UserRole;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    public String generateToken(UserRole userRole) {

        UserAccounts user = userRole.getUserAccount();
        RoleMaster role = userRole.getRoleMaster();

        Map<String, Object> claims = new HashMap<>();

        claims.put("userAccountId", user.getUserAccountId());
        claims.put("employeeId", user.getEmployeeId());
        claims.put("email", user.getEmail());
        claims.put("username", user.getUsername());

        claims.put("roleId", role.getRoleId());
        claims.put("roleName", role.getRoleName());

        return Jwts.builder()
                .claims(claims)
                .subject(user.getUsername())
                .issuedAt(new Date())
                .expiration(
                        new Date(System.currentTimeMillis() + expiration)
                )
                .signWith(getSigningKey())
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public UUID extractUserAccountId(String token) {
        return extractClaim(
                token,
                claims -> UUID.fromString(
                        claims.get("userAccountId", String.class)
                )
        );
    }

    public String extractEmail(String token) {
        return extractClaim(
                token,
                claims -> claims.get("email", String.class)
        );
    }

    public String extractRoleName(String token) {
        return extractClaim(
                token,
                claims -> claims.get("roleName", String.class)
        );
    }

    public UUID extractRoleId(String token) {
        return extractClaim(
                token,
                claims -> UUID.fromString(
                        claims.get("roleId", String.class)
                )
        );
    }

    public <T> T extractClaim(
            String token,
            Function<Claims, T> claimsResolver) {

        Claims claims = extractAllClaims(token);

        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenExpired(String token) {

        return extractClaim(
                token,
                Claims::getExpiration
        ).before(new Date());
    }

    public boolean isTokenValid(
            String token,
            CustomUserDetails userDetails) {

        UUID tokenUserId = extractUserAccountId(token);

        return tokenUserId.equals(
                userDetails.getUserAccountId()
        ) && !isTokenExpired(token);
    }
}