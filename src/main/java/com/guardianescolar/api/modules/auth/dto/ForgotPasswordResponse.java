package com.guardianescolar.api.modules.auth.dto;

import java.util.UUID;

public record ForgotPasswordResponse(
        UUID resetId,
        String method,
        String contact,
        int expiresInMinutes) {
}
