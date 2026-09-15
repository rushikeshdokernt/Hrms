package com.auth.jwt.security;

import java.security.interfaces.RSAPrivateKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth.entity.RoleMaster;
import com.auth.entity.UserAccounts;
import com.auth.entity.UserRole;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final RSAPrivateKey privateKey;

    @Value("${jwt.expiration}")
    private long expiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    public String generateToken(UserRole userRole) {

        UserAccounts user =
                userRole.getUserAccount();

        RoleMaster role =
                userRole.getRoleMaster();

        Map<String, Object> claims =
                new HashMap<>();

        claims.put(
                "userAccountId",
                user.getUserAccountId()
        );

        claims.put(
                "employeeId",
                user.getEmployeeId()
        );

        claims.put(
                "email",
                user.getEmail()
        );

        claims.put(
                "username",
                user.getUsername()
        );

        claims.put(
                "roleId",
                role.getRoleId()
        );

        claims.put(
                "roleName",
                role.getRoleName()
        );

        Date issuedAt = new Date();

        Date expirationDate =
                new Date(
                        issuedAt.getTime() + expiration
                );

        return Jwts.builder()
                .claims(claims)
                .subject(user.getUsername())
                .issuedAt(issuedAt)
                .expiration(expirationDate)
                .signWith(
                        privateKey,
                        Jwts.SIG.RS256
                )
                .compact();
    }
}