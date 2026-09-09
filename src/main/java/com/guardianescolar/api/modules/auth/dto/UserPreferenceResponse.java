package com.guardianescolar.api.modules.auth.dto;

public record UserPreferenceResponse(String language, String timezone, boolean pushEnabled, boolean emailEnabled,
        boolean smsEnabled, boolean locationAlertsEnabled, boolean routeAlertsEnabled, boolean safeZoneAlertsEnabled) {
}
