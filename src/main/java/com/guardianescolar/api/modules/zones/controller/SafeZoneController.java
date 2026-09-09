package com.guardianescolar.api.modules.zones.controller;

import com.guardianescolar.api.modules.zones.dto.SafeZoneRequest;
import com.guardianescolar.api.modules.zones.dto.SafeZoneResponse;
import com.guardianescolar.api.modules.zones.service.SafeZoneService;
import com.guardianescolar.api.shared.security.UserPrincipal;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/students/{studentId}/zones")
public class SafeZoneController {

    private final SafeZoneService service;

    public SafeZoneController(SafeZoneService service) {
        this.service = service;
    }

    @GetMapping
    public List<SafeZoneResponse> list(@AuthenticationPrincipal UserPrincipal principal, @PathVariable UUID studentId) {
        return service.list(principal.id(), studentId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SafeZoneResponse create(@AuthenticationPrincipal UserPrincipal principal, @PathVariable UUID studentId,
            @Valid @RequestBody SafeZoneRequest request) {
        return service.create(principal.id(), studentId, request);
    }

    @PatchMapping("/{zoneId}")
    public SafeZoneResponse update(@AuthenticationPrincipal UserPrincipal principal, @PathVariable UUID zoneId,
            @Valid @RequestBody SafeZoneRequest request) {
        return service.update(principal.id(), zoneId, request);
    }

    @DeleteMapping("/{zoneId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal UserPrincipal principal, @PathVariable UUID zoneId) {
        service.delete(principal.id(), zoneId);
    }
}
