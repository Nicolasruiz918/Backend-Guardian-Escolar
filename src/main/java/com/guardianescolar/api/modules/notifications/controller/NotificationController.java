package com.guardianescolar.api.modules.notifications.controller;

import com.guardianescolar.api.modules.notifications.dto.JourneyHistoryResponse;
import com.guardianescolar.api.modules.notifications.dto.NotificationResponse;
import com.guardianescolar.api.modules.notifications.service.NotificationService;
import com.guardianescolar.api.shared.security.UserPrincipal;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @GetMapping("/notifications")
    public List<NotificationResponse> notifications(@AuthenticationPrincipal UserPrincipal principal) {
        return service.list(principal.id());
    }

    @PatchMapping("/notifications/{notificationId}/read")
    public NotificationResponse markRead(@AuthenticationPrincipal UserPrincipal principal, @PathVariable UUID notificationId) {
        return service.markRead(principal.id(), notificationId);
    }

    @GetMapping("/students/{studentId}/history")
    public List<JourneyHistoryResponse> history(@AuthenticationPrincipal UserPrincipal principal, @PathVariable UUID studentId) {
        return service.history(principal.id(), studentId);
    }
}
