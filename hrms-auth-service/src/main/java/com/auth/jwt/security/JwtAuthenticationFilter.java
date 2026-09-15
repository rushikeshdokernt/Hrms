//package com.auth.jwt.security;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.UUID;
//
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import io.jsonwebtoken.JwtException;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//
//@Component
//@RequiredArgsConstructor
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    private final JwtService jwtService;
//
//    @Override
//    protected void doFilterInternal(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            FilterChain filterChain)
//            throws ServletException, IOException {
//
//        final String authHeader =
//                request.getHeader("Authorization");
//
//        // No JWT
//        if (authHeader == null
//                || !authHeader.startsWith("Bearer ")) {
//
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        try {
//
//            String token = authHeader.substring(7);
//
//            // Extract JWT information
//            UUID userAccountId =
//                    jwtService.extractUserAccountId(token);
//
//            String username =
//                    jwtService.extractUsername(token);
//
//            String roleName =
//                    jwtService.extractRoleName(token);
//
//            UUID roleId =
//                    jwtService.extractRoleId(token);
//
//            // Make sure token contains required information
//            if (userAccountId != null
//                    && username != null
//                    && SecurityContextHolder
//                            .getContext()
//                            .getAuthentication() == null) {
//
//                String authority =
//                        (roleName != null && !roleName.isBlank())
//                                ? "ROLE_" + roleName
//                                : "ROLE_USER";
//
//                CustomUserDetails userDetails =
//                        new CustomUserDetails(
//                                userAccountId,
//                                username,
//                                "",
//                                List.of(
//                                        new SimpleGrantedAuthority(
//                                                authority
//                                        )
//                                )
//                        );
//
//                // Validate JWT
//                if (jwtService.isTokenValid(
//                        token,
//                        userDetails)) {
//
//                    // Optional request attributes
//                    request.setAttribute(
//                            "userAccountId",
//                            userAccountId
//                    );
//
//                    request.setAttribute(
//                            "username",
//                            username
//                    );
//
//                    if (roleName != null) {
//                        request.setAttribute(
//                                "roleName",
//                                roleName
//                        );
//                    }
//
//                    if (roleId != null) {
//                        request.setAttribute(
//                                "roleId",
//                                roleId
//                        );
//                    }
//
//                    // Create authentication
//                    UsernamePasswordAuthenticationToken authentication =
//                            new UsernamePasswordAuthenticationToken(
//                                    userDetails,
//                                    null,
//                                    userDetails.getAuthorities()
//                            );
//
//                    authentication.setDetails(
//                            new WebAuthenticationDetailsSource()
//                                    .buildDetails(request)
//                    );
//
//                    SecurityContextHolder
//                            .getContext()
//                            .setAuthentication(authentication);
//                }
//            }
//
//        } catch (JwtException | IllegalArgumentException ex) {
//
//            /*
//             * Invalid / expired JWT.
//             *
//             * Don't authenticate the request.
//             * Spring Security will decide whether
//             * the endpoint requires authentication.
//             */
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}