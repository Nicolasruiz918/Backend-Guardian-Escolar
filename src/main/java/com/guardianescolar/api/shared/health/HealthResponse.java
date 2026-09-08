package com.guardianescolar.api.shared.health;

import java.time.Instant;

public record HealthResponse(
        String status,
        String service,
        String version,
        Instant timestamp) {
}
