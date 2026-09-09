package com.guardianescolar.api.modules.push.service;

import com.guardianescolar.api.modules.auth.repository.UserPreferenceRepository;
import com.guardianescolar.api.modules.push.domain.PushDevice;
import com.guardianescolar.api.modules.push.repository.PushDeviceRepository;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
public class PushNotificationDispatcher {

    private static final Logger log = LoggerFactory.getLogger(PushNotificationDispatcher.class);

    private final PushDeviceRepository devices;
    private final UserPreferenceRepository preferences;
    private final PushGateway gateway;
    private final PushProperties properties;

    public PushNotificationDispatcher(PushDeviceRepository devices, UserPreferenceRepository preferences,
            PushGateway gateway, PushProperties properties) {
        this.devices = devices;
        this.preferences = preferences;
        this.gateway = gateway;
        this.properties = properties;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void dispatch(PushNotificationEvent event) {
        if (!properties.isEnabled()) {
            return;
        }
        boolean pushEnabled = preferences.findByUserId(event.userId())
                .map(preference -> preference.isPushEnabled())
                .orElse(true);
        if (!pushEnabled) {
            return;
        }

        Map<String, String> data = new LinkedHashMap<>();
        data.put("type", event.type().name());
        if (event.studentId() != null) {
            data.put("studentId", event.studentId().toString());
        }

        devices.findAllByUserIdAndActiveTrue(event.userId()).forEach(device -> send(device, event, data));
    }

    private void send(PushDevice device, PushNotificationEvent event, Map<String, String> data) {
        try {
            gateway.send(new PushMessage(device.getToken(), event.title(), event.body(), data));
        } catch (PushDeliveryException exception) {
            if (exception.isInvalidDevice()) {
                device.deactivate();
            }
            log.warn("Push delivery failed for device {}: {}", device.getId(), exception.getMessage());
        } catch (RuntimeException exception) {
            log.warn("Push delivery failed for device {}: {}", device.getId(), exception.getMessage());
        }
    }
}
