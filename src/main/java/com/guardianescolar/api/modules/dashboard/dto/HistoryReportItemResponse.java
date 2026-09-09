package com.guardianescolar.api.modules.dashboard.dto;

import java.time.Instant;
import java.util.UUID;

public record HistoryReportItemResponse(UUID studentId, double latitude, double longitude, Instant recordedAt,
        String status) {
}
