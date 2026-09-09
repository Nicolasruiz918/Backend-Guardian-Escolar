package com.guardianescolar.api.modules.dashboard.service;

import com.guardianescolar.api.modules.dashboard.dto.DashboardSummaryResponse;
import com.guardianescolar.api.modules.dashboard.dto.HistoryReportItemResponse;
import com.guardianescolar.api.modules.dashboard.dto.HistoryReportResponse;
import com.guardianescolar.api.modules.notifications.repository.GuardianEventRepository;
import com.guardianescolar.api.modules.notifications.repository.GuardianNotificationRepository;
import com.guardianescolar.api.modules.notifications.repository.JourneyHistoryRepository;
import com.guardianescolar.api.modules.students.repository.StudentRepository;
import com.guardianescolar.api.modules.trips.domain.LocationPoint;
import com.guardianescolar.api.modules.trips.repository.LocationPointRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final StudentRepository students;
    private final LocationPointRepository locations;
    private final GuardianEventRepository events;
    private final GuardianNotificationRepository notifications;
    private final JourneyHistoryRepository history;

    public DashboardService(StudentRepository students, LocationPointRepository locations, GuardianEventRepository events,
            GuardianNotificationRepository notifications, JourneyHistoryRepository history) {
        this.students = students;
        this.locations = locations;
        this.events = events;
        this.notifications = notifications;
        this.history = history;
    }

    public DashboardSummaryResponse summary(UUID userId) {
        LocationPoint latest = locations.findTopByStudentOwnerIdOrderByRecordedAtDesc(userId).orElse(null);
        return new DashboardSummaryResponse(students.countByOwnerId(userId), locations.countByStudentOwnerId(userId),
                events.countByStudentOwnerId(userId), notifications.countByUserIdAndReadAtIsNull(userId),
                latest == null ? null : latest.getStudent().getId(), latest == null ? null : latest.getLatitude(),
                latest == null ? null : latest.getLongitude(), latest == null ? null : latest.getRecordedAt());
    }

    public HistoryReportResponse historyReport(UUID userId) {
        return new HistoryReportResponse(history.findTop200ByStudentOwnerIdOrderByRecordedAtDesc(userId).stream()
                .map(item -> new HistoryReportItemResponse(item.getStudent().getId(), item.getLatitude(), item.getLongitude(),
                        item.getRecordedAt(), item.getStatus()))
                .toList());
    }
}
