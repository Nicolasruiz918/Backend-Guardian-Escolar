package com.guardianescolar.api.modules.zones.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

public final class SafeZoneDtos {

    private SafeZoneDtos() {
    }

    public record SafeZoneRequest(
            @NotNull UUID studentId,
            @NotBlank @Size(max = 100) String zoneName,
            @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") BigDecimal latitude,
            @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") BigDecimal longitude,
            @NotNull @Positive @Max(50000) Integer radiusMeters,
            @Positive @Max(86400) Integer inactivityAlertSeconds) {
    }

    public record SafeZoneResponse(
            UUID id,
            UUID studentId,
            String studentName,
            String zoneName,
            BigDecimal latitude,
            BigDecimal longitude,
            Integer radiusMeters,
            Integer inactivityAlertSeconds,
            Boolean isActive) {
    }
}
