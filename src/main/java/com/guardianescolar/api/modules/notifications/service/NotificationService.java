package com.guardianescolar.api.modules.notifications.service;

import com.guardianescolar.api.modules.notifications.domain.GuardianNotification;
import com.guardianescolar.api.modules.notifications.domain.JourneyHistory;
import com.guardianescolar.api.modules.notifications.dto.JourneyHistoryResponse;
import com.guardianescolar.api.modules.notifications.dto.NotificationResponse;
import com.guardianescolar.api.modules.notifications.repository.GuardianNotificationRepository;
import com.guardianescolar.api.modules.notifications.repository.JourneyHistoryRepository;
import com.guardianescolar.api.modules.students.service.StudentService;
import com.guardianescolar.api.shared.exception.ApiException;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class NotificationService {

    private final GuardianNotificationRepository notifications;
    private final JourneyHistoryRepository history;
    private final StudentService studentService;
    private final Clock clock;

    public NotificationService(GuardianNotificationRepository notifications, JourneyHistoryRepository history,
            StudentService studentService, Clock clock) {
        this.notifications = notifications;
        this.history = history;
        this.studentService = studentService;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> list(UUID userId) {
        return notifications.findAllByUserIdOrderByCreatedAtDesc(userId).stream().map(this::toNotification).toList();
    }

    public NotificationResponse markRead(UUID userId, UUID notificationId) {
        GuardianNotification notification = notifications.findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Notification not found"));
        notification.markRead(Instant.now(clock));
        return toNotification(notification);
    }

    @Transactional(readOnly = true)
    public List<JourneyHistoryResponse> history(UUID ownerId, UUID studentId) {
        studentService.findOwned(ownerId, studentId);
        return history.findTop100ByStudentIdOrderByRecordedAtDesc(studentId).stream().map(this::toHistory).toList();
    }

    private NotificationResponse toNotification(GuardianNotification notification) {
        UUID studentId = notification.getStudent() == null ? null : notification.getStudent().getId();
        return new NotificationResponse(notification.getId(), studentId, notification.getTitle(), notification.getBody(),
                notification.getType().name(), notification.getReadAt(), notification.getCreatedAt());
    }

    private JourneyHistoryResponse toHistory(JourneyHistory item) {
        return new JourneyHistoryResponse(item.getId(), item.getStudent().getId(), item.getLatitude(), item.getLongitude(),
                item.getRecordedAt(), item.getStatus());
    }
}
