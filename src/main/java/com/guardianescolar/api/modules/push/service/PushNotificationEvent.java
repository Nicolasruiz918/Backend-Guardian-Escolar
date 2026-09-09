package com.guardianescolar.api.modules.push.service;

import com.guardianescolar.api.modules.notifications.domain.EventType;
import java.util.UUID;

public record PushNotificationEvent(
        UUID userId,
        String title,
        String body,
        EventType type,
        UUID studentId) {
}
