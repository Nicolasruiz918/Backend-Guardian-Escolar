package com.guardianescolar.api.modules.zones.dto;

import java.util.UUID;

public record SafeZoneResponse(UUID id, UUID studentId, String name, double latitude, double longitude,
        double radiusMeters, boolean active) {
}
