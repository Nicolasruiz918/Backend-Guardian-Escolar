package com.guardianescolar.api.modules.students.service;

import com.guardianescolar.api.modules.routes.domain.Route;
import com.guardianescolar.api.modules.routes.domain.Stop;
import com.guardianescolar.api.modules.routes.domain.StudentRoute;
import com.guardianescolar.api.modules.routes.dto.RouteDtos;
import com.guardianescolar.api.modules.routes.repository.StopRepository;
import com.guardianescolar.api.modules.routes.repository.StudentRouteRepository;
import com.guardianescolar.api.modules.security.domain.User;
import com.guardianescolar.api.modules.students.domain.Student;
import com.guardianescolar.api.modules.students.domain.StudentDevice;
import com.guardianescolar.api.modules.students.domain.StudentGuardian;
import com.guardianescolar.api.modules.students.dto.StudentDtos;
import com.guardianescolar.api.modules.students.repository.StudentDeviceRepository;
import com.guardianescolar.api.modules.students.repository.StudentGuardianRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudentMapper {

    private final StudentRouteRepository studentRouteRepository;
    private final StopRepository stopRepository;
    private final StudentDeviceRepository studentDeviceRepository;
    private final StudentGuardianRepository studentGuardianRepository;

    public StudentDtos.StudentResponse toResponse(Student student) {
        return new StudentDtos.StudentResponse(
                student.getId(),
                student.getUser().getId(),
                student.getUser().getFullName(),
                student.getUser().getEmail(),
                student.getFullName(),
                student.getSchoolGrade(),
                student.getBirthDate(),
                student.getIsActive(),
                studentDeviceRepository.countByStudentIdAndIsActiveTrueAndDeletedAtIsNull(student.getId()),
                1 + studentGuardianRepository.countByStudentIdAndStatusAndDeletedAtIsNull(student.getId(), "ACTIVE"),
                assignedRoutes(student.getId()));
    }

    public StudentDtos.LinkedGuardianResponse toGuardianResponse(StudentGuardian access) {
        User user = access.getUser();
        return new StudentDtos.LinkedGuardianResponse(
                access.getId(),
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                access.getRelationshipRole(),
                access.getStatus(),
                access.getCreatedAt());
    }

    public StudentDtos.StudentDeviceResponse toDeviceResponse(StudentDevice device) {
        return new StudentDtos.StudentDeviceResponse(
                device.getId(),
                device.getStudent().getId(),
                device.getPlatform(),
                device.getDeviceName(),
                device.getIsActive(),
                device.getLinkedAt(),
                device.getLastUsedAt(),
                studentDeviceRepository.countByStudentIdAndIsActiveTrueAndDeletedAtIsNull(
                        device.getStudent().getId()));
    }

    private List<RouteDtos.RouteResponse> assignedRoutes(UUID studentId) {
        return studentRouteRepository.findByIdStudentIdAndIsActiveTrue(studentId).stream()
                .map(StudentRoute::getRoute)
                .filter(route -> route.getDeletedAt() == null)
                .map(route -> toRouteResponse(route, stopRepository.findByRouteIdOrderByStopOrderAsc(route.getId())))
                .toList();
    }

    private RouteDtos.RouteResponse toRouteResponse(Route route, List<Stop> stops) {
        return new RouteDtos.RouteResponse(
                route.getId(),
                route.getRouteName(),
                route.getDescription(),
                route.getOriginLatitude(),
                route.getOriginLongitude(),
                route.getDestinationLatitude(),
                route.getDestinationLongitude(),
                stops.stream().map(this::toStopResponse).toList());
    }

    private RouteDtos.StopResponse toStopResponse(Stop stop) {
        return new RouteDtos.StopResponse(
                stop.getId(),
                stop.getStopOrder(),
                stop.getStopName(),
                stop.getLatitude(),
                stop.getLongitude());
    }
}
