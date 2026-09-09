package com.guardianescolar.api.modules.trips.dto;

import java.time.Instant;
import java.util.UUID;

public record StudentDeviceResponse(UUID id, UUID studentId, String name, String identifier, boolean active,
        Instant lastSeenAt) {
}
