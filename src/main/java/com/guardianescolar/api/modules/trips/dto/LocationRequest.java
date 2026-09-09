package com.guardianescolar.api.modules.trips.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record LocationRequest(String deviceIdentifier, @NotNull @Min(-90) @Max(90) Double latitude,
        @NotNull @Min(-180) @Max(180) Double longitude, Double accuracy, Double speed,
        @Min(0) @Max(100) Integer batteryLevel, Instant recordedAt) {
}
