package com.guardianescolar.api.modules.push.controller;

import com.guardianescolar.api.modules.push.dto.PushDeviceRequest;
import com.guardianescolar.api.modules.push.dto.PushDeviceResponse;
import com.guardianescolar.api.modules.push.service.PushDeviceService;
import com.guardianescolar.api.shared.security.UserPrincipal;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/push/devices")
public class PushDeviceController {

    private final PushDeviceService service;

    public PushDeviceController(PushDeviceService service) {
        this.service = service;
    }

    @GetMapping
    public List<PushDeviceResponse> list(@AuthenticationPrincipal UserPrincipal principal) {
        return service.list(principal.id());
    }

    @PutMapping
    public PushDeviceResponse register(@AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody PushDeviceRequest request) {
        return service.register(principal.id(), request);
    }

    @DeleteMapping("/{deviceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal UserPrincipal principal, @PathVariable UUID deviceId) {
        service.delete(principal.id(), deviceId);
    }
}
