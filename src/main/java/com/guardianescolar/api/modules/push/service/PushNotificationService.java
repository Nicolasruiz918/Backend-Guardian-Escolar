package com.guardianescolar.api.modules.push.service;

import com.guardianescolar.api.modules.notifications.domain.EventType;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class PushNotificationService {

    private final ApplicationEventPublisher events;

    public PushNotificationService(ApplicationEventPublisher events) {
        this.events = events;
    }

    public void notifyAfterCommit(UUID userId, String title, String body, EventType type, UUID studentId) {
        events.publishEvent(new PushNotificationEvent(userId, title, body, type, studentId));
    }
}
