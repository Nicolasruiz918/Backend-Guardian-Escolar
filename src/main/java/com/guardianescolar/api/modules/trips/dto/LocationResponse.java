package com.guardianescolar.api.modules.trips.dto;

import java.time.Instant;
import java.util.UUID;

public record LocationResponse(UUID id, UUID studentId, UUID deviceId, double latitude, double longitude, Double accuracy,
        Double speed, Integer batteryLevel, Instant recordedAt) {
}
