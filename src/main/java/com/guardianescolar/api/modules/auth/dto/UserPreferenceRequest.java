package com.guardianescolar.api.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserPreferenceRequest(
        @NotBlank @Size(max = 10) String language,
        @NotBlank @Size(max = 60) String timezone,
        boolean pushEnabled,
        boolean emailEnabled,
        boolean smsEnabled,
        boolean locationAlertsEnabled,
        boolean routeAlertsEnabled,
        boolean safeZoneAlertsEnabled) {
}
