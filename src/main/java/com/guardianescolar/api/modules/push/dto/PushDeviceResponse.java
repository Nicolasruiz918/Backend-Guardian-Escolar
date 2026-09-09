package com.guardianescolar.api.modules.push.dto;

import java.time.Instant;
import java.util.UUID;

public record PushDeviceResponse(
        UUID id,
        String platform,
        String deviceName,
        boolean active,
        Instant lastSeenAt,
        Instant createdAt) {
}
