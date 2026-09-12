package com.guardianescolar.api.modules.notifications.service;

import com.guardianescolar.api.modules.auth.service.CurrentUserService;
import com.guardianescolar.api.modules.notifications.domain.UserDevice;
import com.guardianescolar.api.modules.notifications.dto.NotificationDtos;
import com.guardianescolar.api.modules.notifications.repository.UserDeviceRepository;
import com.guardianescolar.api.modules.security.domain.User;
import com.guardianescolar.api.shared.exception.ResourceNotFoundException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDeviceService {

    private final UserDeviceRepository userDeviceRepository;
    private final CurrentUserService currentUserService;

    @Transactional
    public NotificationDtos.DeviceResponse registrar(NotificationDtos.DeviceRequest request) {
        User actual = currentUserService.currentUser();
        UserDevice dispositivo = userDeviceRepository.findByExpoPushToken(request.expoPushToken())
                .orElseGet(UserDevice::new);

        if (dispositivo.getUser() != null && !dispositivo.getUser().getId().equals(actual.getId())) {
            throw new AccessDeniedException("Este dispositivo ya está asociado a otro User");
        }

        dispositivo.setUser(actual);
        dispositivo.setExpoPushToken(request.expoPushToken().trim());
        dispositivo.setPlatform(request.platform().trim().toUpperCase());
        dispositivo.setDeviceName(normalizarTextoOpcional(request.deviceName()));
        dispositivo.setIsActive(true);
        dispositivo.setLastUsedAt(OffsetDateTime.now());
        dispositivo.setDeletedAt(null);
        return toResponse(userDeviceRepository.save(dispositivo));
    }

    @Transactional(readOnly = true)
    public List<NotificationDtos.DeviceResponse> listarActuales() {
        User actual = currentUserService.currentUser();
        return userDeviceRepository.findByUserIdAndIsActiveTrueAndDeletedAtIsNull(actual.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void desactivar(UUID dispositivoId) {
        User actual = currentUserService.currentUser();
        UserDevice dispositivo = userDeviceRepository.findByIdAndUserId(dispositivoId, actual.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Dispositivo no encontrado"));
        dispositivo.setIsActive(false);
        dispositivo.setDeletedAt(OffsetDateTime.now());
        userDeviceRepository.save(dispositivo);
    }

    private NotificationDtos.DeviceResponse toResponse(UserDevice dispositivo) {
        return new NotificationDtos.DeviceResponse(
                dispositivo.getId(),
                dispositivo.getExpoPushToken(),
                dispositivo.getPlatform(),
                dispositivo.getDeviceName(),
                dispositivo.getIsActive(),
                dispositivo.getLastUsedAt());
    }

    private String normalizarTextoOpcional(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
