package com.guardianescolar.api.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequest(
        @NotBlank String method,
        @NotBlank String contact) {
}
