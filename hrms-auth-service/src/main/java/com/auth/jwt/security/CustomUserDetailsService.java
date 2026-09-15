package com.auth.jwt.security;


import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.auth.entity.UserAccounts;
import com.auth.entity.UserRole;
import com.auth.exception.ResourceNotFoundException;
import com.auth.repository.UserAccountsRepository;
import com.auth.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserAccountsRepository userAccountsRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        UserAccounts user = userAccountsRepository
                .findByEmail(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found: " + username
                        )
                );

        UserRole userRole =
                userRoleRepository
                        .findByUserAccountsUserAccountId(
                                user.getUserAccountId()
                        ).orElseThrow(()-> new ResourceNotFoundException("User not found"));

        List<SimpleGrantedAuthority> authorities =
                userRole.getRoleMaster() != null
                        ? List.of(
                                new SimpleGrantedAuthority(
                                        "ROLE_" +
                                        userRole.getRoleMaster().getRoleName()
                                )
                        )
                        : List.of();

        return new CustomUserDetails(
                user.getUserAccountId(),
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}
