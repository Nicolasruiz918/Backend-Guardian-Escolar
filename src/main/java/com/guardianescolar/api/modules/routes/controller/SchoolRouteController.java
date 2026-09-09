package com.guardianescolar.api.modules.routes.controller;

import com.guardianescolar.api.modules.routes.dto.SchoolRouteRequest;
import com.guardianescolar.api.modules.routes.dto.SchoolRouteResponse;
import com.guardianescolar.api.modules.routes.service.SchoolRouteService;
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
@RequestMapping("/students/{studentId}/routes")
public class SchoolRouteController {

    private final SchoolRouteService service;

    public SchoolRouteController(SchoolRouteService service) {
        this.service = service;
    }

    @GetMapping
    public List<SchoolRouteResponse> list(@AuthenticationPrincipal UserPrincipal principal, @PathVariable UUID studentId) {
        return service.list(principal.id(), studentId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SchoolRouteResponse create(@AuthenticationPrincipal UserPrincipal principal, @PathVariable UUID studentId,
            @Valid @RequestBody SchoolRouteRequest request) {
        return service.create(principal.id(), studentId, request);
    }

    @PatchMapping("/{routeId}")
    public SchoolRouteResponse update(@AuthenticationPrincipal UserPrincipal principal, @PathVariable UUID routeId,
            @Valid @RequestBody SchoolRouteRequest request) {
        return service.update(principal.id(), routeId, request);
    }

    @DeleteMapping("/{routeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal UserPrincipal principal, @PathVariable UUID routeId) {
        service.delete(principal.id(), routeId);
    }
}
