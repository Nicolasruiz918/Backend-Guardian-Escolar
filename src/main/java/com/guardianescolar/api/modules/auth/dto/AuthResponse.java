package com.guardianescolar.api.modules.auth.dto;

import java.util.UUID;

public record AuthResponse(
        String token,
        String accessToken,
        String refreshToken,
        UserSessionResponse user,
        boolean requires2FA,
        UUID challengeId,
        String method,
        String email) {
}
