package com.guardianescolar.api.modules.auth.dto;

import java.util.UUID;

public record UserSessionResponse(
        UUID id,
        String email,
        String name,
        String phone,
        String role,
        boolean twoFAEnabled,
        String twoFAMethod) {
}
