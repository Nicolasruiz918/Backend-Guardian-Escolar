package com.guardianescolar.api.modules.routes.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public record SchoolRouteRequest(@NotBlank @Size(max = 120) String name, @Size(max = 160) String originName,
        @Size(max = 160) String destinationName, Boolean active, List<@Valid RoutePointRequest> points) {
}
