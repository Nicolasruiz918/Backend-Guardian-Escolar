package com.guardianescolar.api.modules.trips.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StudentDeviceRequest(@NotBlank @Size(max = 120) String name,
        @NotBlank @Size(max = 120) String identifier, Boolean active) {
}
