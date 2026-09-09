package com.guardianescolar.api.modules.auth.dto;

import jakarta.validation.constraints.NotNull;

public record TwoFactorSettingsRequest(
        @NotNull Boolean enabled,
        String method) {
}
