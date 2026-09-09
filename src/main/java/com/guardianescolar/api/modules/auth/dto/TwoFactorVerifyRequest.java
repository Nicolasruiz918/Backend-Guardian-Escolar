package com.guardianescolar.api.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.UUID;

public record TwoFactorVerifyRequest(
        @NotNull UUID challengeId,
        @NotBlank @Pattern(regexp = "\\d{6}") String code) {
}
