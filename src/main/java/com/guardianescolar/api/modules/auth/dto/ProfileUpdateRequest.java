package com.guardianescolar.api.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProfileUpdateRequest(
        @NotBlank @Size(max = 160) String name,
        @Size(max = 40) String phone) {
}
