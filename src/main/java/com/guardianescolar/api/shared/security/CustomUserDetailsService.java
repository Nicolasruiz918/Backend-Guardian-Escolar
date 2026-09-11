package com.guardianescolar.api.shared.security;

import com.guardianescolar.api.modules.security.domain.User;
import com.guardianescolar.api.modules.security.repository.RolePermissionRepository;
import com.guardianescolar.api.modules.security.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailIgnoreCase(email)
                .filter(User::getIsActive)
                .orElseThrow(() -> new UsernameNotFoundException("User no encontrado"));

        List<SimpleGrantedAuthority> authorities = new ArrayList<>(user.getRoles().stream()
                 .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName().name()))
                .toList());

        Set<UUID> roleIds = user.getRoles().stream()
                 .map(role -> role.getId())
                .collect(Collectors.toSet());

        if (!roleIds.isEmpty()) {
            rolePermissionRepository.findPermissionNamesByRoleIds(roleIds).stream()
                    .map(SimpleGrantedAuthority::new)
                    .forEach(authorities::add);
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPasswordHash())
                .authorities(authorities)
                .accountLocked(false)
                .disabled(!Boolean.TRUE.equals(user.getIsActive())
                        || !Boolean.TRUE.equals(user.getEmailVerified()))
                .build();
    }
}
