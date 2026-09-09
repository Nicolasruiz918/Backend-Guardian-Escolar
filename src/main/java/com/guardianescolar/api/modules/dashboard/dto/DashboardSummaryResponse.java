package com.guardianescolar.api.modules.dashboard.dto;

import java.time.Instant;
import java.util.UUID;

public record DashboardSummaryResponse(long students, long locations, long events, long unreadNotifications,
        UUID latestStudentId, Double latestLatitude, Double latestLongitude, Instant latestRecordedAt) {
}
