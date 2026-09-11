package com.guardianescolar.api.modules.auth.service;

import com.guardianescolar.api.modules.auth.dto.AuthDtos;
import com.guardianescolar.api.modules.security.domain.User;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class AuthUserMapper {

    public AuthDtos.UserResponse toUserResponse(User user) {
        Set<String> roles = user.getRoles().stream()
                .map(role -> role.getRoleName().name())
                .collect(java.util.stream.Collectors.toSet());
        return new AuthDtos.UserResponse(
                user.getId(),
                user.getFullName(),
                user.getPhone(),
                user.getEmail(),
                user.getEmailVerified(),
                Boolean.TRUE.equals(user.getTwoFactorEnabled()),
                user.getTwoFactorMethod(),
                user.getTermsAccepted(),
                user.getTermsVersion(),
                roles);
    }
}
