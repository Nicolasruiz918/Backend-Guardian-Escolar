package com.guardianescolar.api.modules.push.service;

import com.guardianescolar.api.modules.auth.domain.UserAccount;
import com.guardianescolar.api.modules.auth.repository.UserAccountRepository;
import com.guardianescolar.api.modules.push.domain.PushDevice;
import com.guardianescolar.api.modules.push.dto.PushDeviceRequest;
import com.guardianescolar.api.modules.push.dto.PushDeviceResponse;
import com.guardianescolar.api.modules.push.repository.PushDeviceRepository;
import com.guardianescolar.api.shared.exception.ApiException;
import java.time.Clock;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PushDeviceService {

    private final PushDeviceRepository devices;
    private final UserAccountRepository users;
    private final Clock clock;

    public PushDeviceService(PushDeviceRepository devices, UserAccountRepository users, Clock clock) {
        this.devices = devices;
        this.users = users;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public List<PushDeviceResponse> list(UUID userId) {
        return devices.findAllByUserIdOrderByCreatedAtDesc(userId).stream().map(this::toResponse).toList();
    }

    public PushDeviceResponse register(UUID userId, PushDeviceRequest request) {
        UserAccount user = users.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
        String token = request.token().trim();
        String deviceName = normalizeDeviceName(request.deviceName());
        PushDevice device = devices.findByToken(token)
                .orElseGet(() -> new PushDevice(user, token, request.platform(), deviceName, clock.instant()));
        device.register(user, request.platform(), deviceName, clock.instant());
        return toResponse(devices.save(device));
    }

    public void delete(UUID userId, UUID deviceId) {
        PushDevice device = devices.findByIdAndUserId(deviceId, userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Push device not found"));
        devices.delete(device);
    }

    private String normalizeDeviceName(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private PushDeviceResponse toResponse(PushDevice device) {
        return new PushDeviceResponse(device.getId(), device.getPlatform().name(), device.getDeviceName(),
                device.isActive(), device.getLastSeenAt(), device.getCreatedAt());
    }
}
