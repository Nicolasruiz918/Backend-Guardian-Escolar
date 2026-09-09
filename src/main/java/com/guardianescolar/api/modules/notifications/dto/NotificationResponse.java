package com.guardianescolar.api.modules.notifications.dto;

import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(UUID id, UUID studentId, String title, String body, String type, Instant readAt,
        Instant createdAt) {
}
