package com.guardianescolar.api.modules.push.dto;

import com.guardianescolar.api.modules.push.domain.PushPlatform;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PushDeviceRequest(
        @NotBlank @Size(max = 255) String token,
        @NotNull PushPlatform platform,
        @Size(max = 120) String deviceName) {
}
