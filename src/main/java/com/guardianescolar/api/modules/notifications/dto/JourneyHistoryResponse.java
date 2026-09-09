package com.guardianescolar.api.modules.notifications.dto;

import java.time.Instant;
import java.util.UUID;

public record JourneyHistoryResponse(UUID id, UUID studentId, double latitude, double longitude, Instant recordedAt,
        String status) {
}
