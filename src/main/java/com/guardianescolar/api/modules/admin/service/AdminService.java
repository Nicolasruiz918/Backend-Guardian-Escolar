package com.guardianescolar.api.modules.admin.service;

import com.guardianescolar.api.modules.admin.dto.AdminDtos;
import com.guardianescolar.api.modules.audit.domain.AuditLog;
import com.guardianescolar.api.modules.audit.domain.ErrorLog;
import com.guardianescolar.api.modules.audit.repository.AuditLogRepository;
import com.guardianescolar.api.modules.audit.repository.ErrorLogRepository;
import com.guardianescolar.api.modules.students.repository.StudentRepository;
import com.guardianescolar.api.modules.notifications.domain.Notification;
import com.guardianescolar.api.modules.notifications.repository.NotificationRepository;
import com.guardianescolar.api.modules.security.domain.User;
import com.guardianescolar.api.modules.security.repository.UserRepository;
import com.guardianescolar.api.modules.trips.domain.TripStatus;
import com.guardianescolar.api.modules.trips.repository.TripRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private static final int DEFAULT_LIMIT = 25;
    private static final int MAX_LIMIT = 100;

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TripRepository tripRepository;
    private final NotificationRepository notificationRepository;
    private final AuditLogRepository auditLogRepository;
    private final ErrorLogRepository errorLogRepository;

    @Transactional(readOnly = true)
    public AdminDtos.DashboardResponse dashboard() {
        return new AdminDtos.DashboardResponse(
                userRepository.count(),
                studentRepository.count(),
                tripRepository.countByStatusAndDeletedAtIsNull(TripStatus.IN_PROGRESS),
                notificationRepository.count(),
                errorLogRepository.count());
    }

    @Transactional(readOnly = true)
    public List<AdminDtos.AlertSummaryResponse> recentAlerts(Integer limit) {
        return notificationRepository.findByOrderByEventDateTimeDesc(pageable(limit)).stream()
                .map(this::toAlertResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AdminDtos.AuditResponse> recentAudit(Integer limit) {
        return auditLogRepository.findByOrderByCreatedAtDesc(pageable(limit)).stream()
                .map(this::toAuditResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AdminDtos.ErrorResponse> recentErrors(Integer limit) {
        return errorLogRepository.findByOrderByCreatedAtDesc(pageable(limit)).stream()
                .map(this::toErrorResponse)
                .toList();
    }

    private Pageable pageable(Integer limit) {
        int size = limit == null ? DEFAULT_LIMIT : Math.max(1, Math.min(limit, MAX_LIMIT));
        return PageRequest.of(0, size);
    }

    private AdminDtos.AlertSummaryResponse toAlertResponse(Notification notification) {
        return new AdminDtos.AlertSummaryResponse(
                notification.getId(),
                notification.getTrip() == null ? null : notification.getTrip().getId(),
                notification.getEventType(),
                notification.getMessage(),
                notification.getEventDateTime(),
                notification.getWasSent());
    }

    private AdminDtos.AuditResponse toAuditResponse(AuditLog auditLog) {
        User user = auditLog.getUser();
        return new AdminDtos.AuditResponse(
                auditLog.getId(),
                user == null ? null : user.getId(),
                user == null ? null : user.getEmail(),
                auditLog.getAccion(),
                auditLog.getDescription(),
                auditLog.getIpOrigen() == null ? null : auditLog.getIpOrigen().getHostAddress(),
                auditLog.getAplicacion(),
                auditLog.getMetadatos(),
                auditLog.getCreatedAt());
    }

    private AdminDtos.ErrorResponse toErrorResponse(ErrorLog error) {
        User user = error.getUser();
        return new AdminDtos.ErrorResponse(
                error.getId(),
                user == null ? null : user.getId(),
                user == null ? null : user.getEmail(),
                error.getTipoError(),
                error.getDescription(),
                error.getIpOrigen() == null ? null : error.getIpOrigen().getHostAddress(),
                error.getMetadatos(),
                error.getCreatedAt());
    }
}
