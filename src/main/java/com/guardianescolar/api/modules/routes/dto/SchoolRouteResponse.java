package com.guardianescolar.api.modules.routes.dto;

import java.util.List;
import java.util.UUID;

public record SchoolRouteResponse(UUID id, UUID studentId, String name, String originName, String destinationName,
        boolean active, List<RoutePointResponse> points) {
}
