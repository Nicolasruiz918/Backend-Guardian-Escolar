package com.guardianescolar.api.modules.zones.service;

import com.guardianescolar.api.modules.students.domain.Student;
import com.guardianescolar.api.modules.students.service.StudentService;
import com.guardianescolar.api.modules.zones.domain.SafeZone;
import com.guardianescolar.api.modules.zones.dto.SafeZoneRequest;
import com.guardianescolar.api.modules.zones.dto.SafeZoneResponse;
import com.guardianescolar.api.modules.zones.repository.SafeZoneRepository;
import com.guardianescolar.api.shared.exception.ApiException;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SafeZoneService {

    private final StudentService studentService;
    private final SafeZoneRepository zones;

    public SafeZoneService(StudentService studentService, SafeZoneRepository zones) {
        this.studentService = studentService;
        this.zones = zones;
    }

    @Transactional(readOnly = true)
    public List<SafeZoneResponse> list(UUID ownerId, UUID studentId) {
        studentService.findOwned(ownerId, studentId);
        return zones.findAllByStudentIdOrderByNameAsc(studentId).stream().map(this::toResponse).toList();
    }

    public SafeZoneResponse create(UUID ownerId, UUID studentId, SafeZoneRequest request) {
        Student student = studentService.findOwned(ownerId, studentId);
        SafeZone zone = new SafeZone(student, request.name().trim());
        apply(zone, request);
        return toResponse(zones.save(zone));
    }

    public SafeZoneResponse update(UUID ownerId, UUID zoneId, SafeZoneRequest request) {
        SafeZone zone = findOwned(ownerId, zoneId);
        apply(zone, request);
        return toResponse(zone);
    }

    public void delete(UUID ownerId, UUID zoneId) {
        zones.delete(findOwned(ownerId, zoneId));
    }

    public SafeZone findOwned(UUID ownerId, UUID zoneId) {
        return zones.findByIdAndStudentOwnerId(zoneId, ownerId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Safe zone not found"));
    }

    private void apply(SafeZone zone, SafeZoneRequest request) {
        zone.update(request.name().trim(), request.latitude(), request.longitude(), request.radiusMeters(),
                request.active() == null || request.active());
    }

    private SafeZoneResponse toResponse(SafeZone zone) {
        return new SafeZoneResponse(zone.getId(), zone.getStudent().getId(), zone.getName(), zone.getCenterLatitude(),
                zone.getCenterLongitude(), zone.getRadiusMeters(), zone.isActive());
    }
}
