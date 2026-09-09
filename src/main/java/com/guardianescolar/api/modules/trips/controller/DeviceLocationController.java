package com.guardianescolar.api.modules.trips.controller;

import com.guardianescolar.api.modules.trips.dto.LocationRequest;
import com.guardianescolar.api.modules.trips.dto.LocationResponse;
import com.guardianescolar.api.modules.trips.dto.StudentDeviceRequest;
import com.guardianescolar.api.modules.trips.dto.StudentDeviceResponse;
import com.guardianescolar.api.modules.trips.service.DeviceLocationService;
import com.guardianescolar.api.shared.security.UserPrincipal;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/students/{studentId}")
public class DeviceLocationController {

    private final DeviceLocationService service;

    public DeviceLocationController(DeviceLocationService service) {
        this.service = service;
    }

    @GetMapping("/devices")
    public List<StudentDeviceResponse> devices(@AuthenticationPrincipal UserPrincipal principal, @PathVariable UUID studentId) {
        return service.devices(principal.id(), studentId);
    }

    @PostMapping("/devices")
    @ResponseStatus(HttpStatus.CREATED)
    public StudentDeviceResponse createDevice(@AuthenticationPrincipal UserPrincipal principal, @PathVariable UUID studentId,
            @Valid @RequestBody StudentDeviceRequest request) {
        return service.createDevice(principal.id(), studentId, request);
    }

    @PatchMapping("/devices/{deviceId}")
    public StudentDeviceResponse updateDevice(@AuthenticationPrincipal UserPrincipal principal, @PathVariable UUID studentId,
            @PathVariable UUID deviceId, @Valid @RequestBody StudentDeviceRequest request) {
        return service.updateDevice(principal.id(), studentId, deviceId, request);
    }

    @PostMapping("/locations")
    @ResponseStatus(HttpStatus.CREATED)
    public LocationResponse receiveLocation(@AuthenticationPrincipal UserPrincipal principal, @PathVariable UUID studentId,
            @Valid @RequestBody LocationRequest request) {
        return service.receiveLocation(principal.id(), studentId, request);
    }

    @GetMapping("/locations/latest")
    public LocationResponse latest(@AuthenticationPrincipal UserPrincipal principal, @PathVariable UUID studentId) {
        return service.latest(principal.id(), studentId);
    }

    @GetMapping("/locations")
    public List<LocationResponse> recent(@AuthenticationPrincipal UserPrincipal principal, @PathVariable UUID studentId) {
        return service.recent(principal.id(), studentId);
    }
}
