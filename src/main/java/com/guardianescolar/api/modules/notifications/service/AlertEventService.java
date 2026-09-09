package com.guardianescolar.api.modules.notifications.service;

import com.guardianescolar.api.modules.auth.domain.UserAccount;
import com.guardianescolar.api.modules.notifications.domain.EventSeverity;
import com.guardianescolar.api.modules.notifications.domain.EventType;
import com.guardianescolar.api.modules.notifications.domain.GuardianEvent;
import com.guardianescolar.api.modules.notifications.domain.GuardianNotification;
import com.guardianescolar.api.modules.notifications.domain.JourneyHistory;
import com.guardianescolar.api.modules.notifications.repository.GuardianEventRepository;
import com.guardianescolar.api.modules.notifications.repository.GuardianNotificationRepository;
import com.guardianescolar.api.modules.notifications.repository.JourneyHistoryRepository;
import com.guardianescolar.api.modules.push.service.PushNotificationService;
import com.guardianescolar.api.modules.trips.domain.LocationPoint;
import com.guardianescolar.api.modules.zones.domain.SafeZone;
import com.guardianescolar.api.modules.zones.repository.SafeZoneRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AlertEventService {

    private final SafeZoneRepository zones;
    private final GuardianEventRepository events;
    private final GuardianNotificationRepository notifications;
    private final JourneyHistoryRepository history;
    private final PushNotificationService pushNotifications;

    public AlertEventService(SafeZoneRepository zones, GuardianEventRepository events,
            GuardianNotificationRepository notifications, JourneyHistoryRepository history,
            PushNotificationService pushNotifications) {
        this.zones = zones;
        this.events = events;
        this.notifications = notifications;
        this.history = history;
        this.pushNotifications = pushNotifications;
    }

    public void evaluate(LocationPoint location) {
        List<SafeZone> activeZones = zones.findAllByStudentIdAndActiveTrue(location.getStudent().getId());
        boolean insideAnyZone = activeZones.stream().anyMatch(zone -> isInside(zone, location));
        EventType type = insideAnyZone || activeZones.isEmpty() ? EventType.LOCATION_RECORDED : EventType.OUTSIDE_SAFE_ZONE;
        EventSeverity severity = type == EventType.OUTSIDE_SAFE_ZONE ? EventSeverity.WARNING : EventSeverity.INFO;
        String message = type == EventType.OUTSIDE_SAFE_ZONE
                ? location.getStudent().getFullName() + " esta fuera de una zona segura"
                : "Ubicacion registrada";
        GuardianEvent event = events.save(new GuardianEvent(location.getStudent(), location, type, message, severity,
                location.getRecordedAt()));
        history.save(new JourneyHistory(location.getStudent(), location, event, type.name()));
        if (type == EventType.OUTSIDE_SAFE_ZONE) {
            UserAccount owner = location.getStudent().getOwner();
            GuardianNotification notification = notifications.save(new GuardianNotification(owner, location.getStudent(), event,
                    "Alerta de zona segura", message, type));
            pushNotifications.notifyAfterCommit(owner.getId(), notification.getTitle(), notification.getBody(),
                    notification.getType(), location.getStudent().getId());
        }
    }

    private boolean isInside(SafeZone zone, LocationPoint point) {
        return distanceMeters(zone.getCenterLatitude(), zone.getCenterLongitude(), point.getLatitude(), point.getLongitude())
                <= zone.getRadiusMeters();
    }

    private double distanceMeters(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 6371000.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return earthRadius * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
