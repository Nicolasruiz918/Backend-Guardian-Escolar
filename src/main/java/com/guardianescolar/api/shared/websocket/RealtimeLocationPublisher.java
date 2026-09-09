package com.guardianescolar.api.shared.websocket;

import com.guardianescolar.api.modules.trips.dto.LocationResponse;
import java.util.UUID;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class RealtimeLocationPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public RealtimeLocationPublisher(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void publish(UUID ownerId, LocationResponse location) {
        messagingTemplate.convertAndSend("/topic/students/" + location.studentId() + "/locations", location);
        messagingTemplate.convertAndSend("/topic/users/" + ownerId + "/locations", location);
    }
}
