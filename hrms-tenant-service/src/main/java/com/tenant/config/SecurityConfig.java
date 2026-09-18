package com.tenant.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // Tenant-service is an internal microservice — CSRF not needed for REST
                .csrf(AbstractHttpConfigurer::disable)

                // Stateless — no session management needed
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // Internal Feign endpoints (called by auth-service)
                        .requestMatchers("/api/v1/tenant/internal/**").permitAll()

                        // Tenant management APIs (add/update/delete)
                        .requestMatchers("/api/v1/tenant/**").permitAll()

                        // Actuator health probe
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()

                        // Everything else requires authentication
                        .anyRequest().authenticated()
                )

                .build();
    }
}
