package com.guardianescolar.api.modules.routes.service;

import com.guardianescolar.api.modules.routes.domain.RoutePoint;
import com.guardianescolar.api.modules.routes.domain.SchoolRoute;
import com.guardianescolar.api.modules.routes.dto.RoutePointRequest;
import com.guardianescolar.api.modules.routes.dto.RoutePointResponse;
import com.guardianescolar.api.modules.routes.dto.SchoolRouteRequest;
import com.guardianescolar.api.modules.routes.dto.SchoolRouteResponse;
import com.guardianescolar.api.modules.routes.repository.SchoolRouteRepository;
import com.guardianescolar.api.modules.students.domain.Student;
import com.guardianescolar.api.modules.students.service.StudentService;
import com.guardianescolar.api.shared.exception.ApiException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SchoolRouteService {

    private final StudentService studentService;
    private final SchoolRouteRepository routes;

    public SchoolRouteService(StudentService studentService, SchoolRouteRepository routes) {
        this.studentService = studentService;
        this.routes = routes;
    }

    @Transactional(readOnly = true)
    public List<SchoolRouteResponse> list(UUID ownerId, UUID studentId) {
        studentService.findOwned(ownerId, studentId);
        return routes.findAllByStudentIdOrderByNameAsc(studentId).stream().map(this::toResponse).toList();
    }

    public SchoolRouteResponse create(UUID ownerId, UUID studentId, SchoolRouteRequest request) {
        Student student = studentService.findOwned(ownerId, studentId);
        SchoolRoute route = new SchoolRoute(student, request.name().trim());
        apply(route, request);
        return toResponse(routes.save(route));
    }

    public SchoolRouteResponse update(UUID ownerId, UUID routeId, SchoolRouteRequest request) {
        SchoolRoute route = findOwned(ownerId, routeId);
        apply(route, request);
        return toResponse(route);
    }

    public void delete(UUID ownerId, UUID routeId) {
        routes.delete(findOwned(ownerId, routeId));
    }

    private SchoolRoute findOwned(UUID ownerId, UUID routeId) {
        return routes.findByIdAndStudentOwnerId(routeId, ownerId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Route not found"));
    }

    private void apply(SchoolRoute route, SchoolRouteRequest request) {
        route.update(request.name().trim(), normalize(request.originName()), normalize(request.destinationName()),
                request.active() == null || request.active());
        List<RoutePoint> points = new ArrayList<>();
        List<RoutePointRequest> requested = request.points() == null ? List.of() : request.points();
        for (int index = 0; index < requested.size(); index++) {
            RoutePointRequest point = requested.get(index);
            points.add(new RoutePoint(route, index + 1, point.latitude(), point.longitude()));
        }
        route.replacePoints(points);
    }

    private SchoolRouteResponse toResponse(SchoolRoute route) {
        return new SchoolRouteResponse(route.getId(), route.getStudent().getId(), route.getName(), route.getOriginName(),
                route.getDestinationName(), route.isActive(),
                route.getPoints().stream()
                        .map(point -> new RoutePointResponse(point.getSequenceNumber(), point.getLatitude(), point.getLongitude()))
                        .toList());
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
