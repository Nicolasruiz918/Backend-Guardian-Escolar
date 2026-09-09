package com.guardianescolar.api.modules.trips.service;

import com.guardianescolar.api.modules.students.domain.Student;
import com.guardianescolar.api.modules.students.service.StudentService;
import com.guardianescolar.api.modules.trips.domain.LocationPoint;
import com.guardianescolar.api.modules.trips.domain.StudentDevice;
import com.guardianescolar.api.modules.trips.dto.LocationRequest;
import com.guardianescolar.api.modules.trips.dto.LocationResponse;
import com.guardianescolar.api.modules.trips.dto.StudentDeviceRequest;
import com.guardianescolar.api.modules.trips.dto.StudentDeviceResponse;
import com.guardianescolar.api.modules.trips.repository.LocationPointRepository;
import com.guardianescolar.api.modules.trips.repository.StudentDeviceRepository;
import com.guardianescolar.api.shared.exception.ApiException;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DeviceLocationService {

    private final StudentService studentService;
    private final StudentDeviceRepository devices;
    private final LocationPointRepository locations;
    private final Clock clock;

    public DeviceLocationService(StudentService studentService, StudentDeviceRepository devices,
            LocationPointRepository locations, Clock clock) {
        this.studentService = studentService;
        this.devices = devices;
        this.locations = locations;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public List<StudentDeviceResponse> devices(UUID ownerId, UUID studentId) {
        studentService.findOwned(ownerId, studentId);
        return devices.findAllByStudentIdOrderByNameAsc(studentId).stream().map(this::toDevice).toList();
    }

    public StudentDeviceResponse createDevice(UUID ownerId, UUID studentId, StudentDeviceRequest request) {
        Student student = studentService.findOwned(ownerId, studentId);
        StudentDevice device = new StudentDevice(student, request.name().trim(), request.identifier().trim());
        device.update(request.name().trim(), request.active() == null || request.active());
        return toDevice(devices.save(device));
    }

    public StudentDeviceResponse updateDevice(UUID ownerId, UUID studentId, UUID deviceId, StudentDeviceRequest request) {
        studentService.findOwned(ownerId, studentId);
        StudentDevice device = devices.findByIdAndStudentId(deviceId, studentId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Device not found"));
        device.update(request.name().trim(), request.active() == null || request.active());
        return toDevice(device);
    }

    public LocationResponse receiveLocation(UUID ownerId, UUID studentId, LocationRequest request) {
        Student student = studentService.findOwned(ownerId, studentId);
        StudentDevice device = resolveDevice(ownerId, request.deviceIdentifier());
        Instant recordedAt = request.recordedAt() == null ? Instant.now(clock) : request.recordedAt();
        if (device != null) {
            device.markSeen(recordedAt);
        }
        LocationPoint point = new LocationPoint(student, device, request.latitude(), request.longitude(), request.accuracy(),
                request.speed(), request.batteryLevel(), recordedAt);
        return afterLocationSaved(toLocation(locations.save(point)));
    }

    @Transactional(readOnly = true)
    public LocationResponse latest(UUID ownerId, UUID studentId) {
        studentService.findOwned(ownerId, studentId);
        return locations.findTopByStudentIdOrderByRecordedAtDesc(studentId)
                .map(this::toLocation)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Location not found"));
    }

    @Transactional(readOnly = true)
    public List<LocationResponse> recent(UUID ownerId, UUID studentId) {
        studentService.findOwned(ownerId, studentId);
        return locations.findTop50ByStudentIdOrderByRecordedAtDesc(studentId).stream().map(this::toLocation).toList();
    }

    protected LocationResponse afterLocationSaved(LocationResponse response) {
        return response;
    }

    private StudentDevice resolveDevice(UUID ownerId, String identifier) {
        if (identifier == null || identifier.isBlank()) {
            return null;
        }
        return devices.findByIdentifierAndStudentOwnerId(identifier.trim(), ownerId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Device not found"));
    }

    private StudentDeviceResponse toDevice(StudentDevice device) {
        return new StudentDeviceResponse(device.getId(), device.getStudent().getId(), device.getName(), device.getIdentifier(),
                device.isActive(), device.getLastSeenAt());
    }

    protected LocationResponse toLocation(LocationPoint point) {
        UUID deviceId = point.getDevice() == null ? null : point.getDevice().getId();
        return new LocationResponse(point.getId(), point.getStudent().getId(), deviceId, point.getLatitude(),
                point.getLongitude(), point.getAccuracy(), point.getSpeed(), point.getBatteryLevel(), point.getRecordedAt());
    }
}
