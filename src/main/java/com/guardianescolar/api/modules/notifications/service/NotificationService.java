package com.guardianescolar.api.modules.notifications.service;

import com.guardianescolar.api.modules.auth.service.CurrentUserService;
import com.guardianescolar.api.modules.auth.service.EmailService;
import com.guardianescolar.api.modules.students.domain.EmergencyContact;
import com.guardianescolar.api.modules.students.repository.EmergencyContactRepository;
import com.guardianescolar.api.modules.notifications.domain.NotificationSettings;
import com.guardianescolar.api.modules.notifications.domain.Notification;
import com.guardianescolar.api.modules.notifications.domain.NotificationEventTypes;
import com.guardianescolar.api.modules.notifications.domain.NotificationReceipt;
import com.guardianescolar.api.modules.notifications.dto.NotificationDtos;
import com.guardianescolar.api.modules.notifications.repository.NotificationSettingsRepository;
import com.guardianescolar.api.modules.notifications.repository.NotificationRepository;
import com.guardianescolar.api.modules.notifications.repository.NotificationReceiptRepository;
import com.guardianescolar.api.modules.security.domain.User;
import com.guardianescolar.api.modules.trips.domain.Trip;
import com.guardianescolar.api.modules.zones.domain.SafeZone;
import com.guardianescolar.api.shared.exception.ResourceNotFoundException;
import java.util.List;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationReceiptRepository notificationReceiptRepository;
    private final NotificationSettingsRepository notificationSettingsRepository;
    private final CurrentUserService currentUserService;
    private final ExpoPushService expoPushService;
    private final EmailService emailService;
    private final SmsService smsService;
    private final EmergencyContactRepository emergencyContactRepository;

    @Transactional(readOnly = true)
    public List<NotificationDtos.NotificationResponse> listForCurrentUser() {
        User current = currentUserService.currentUser();
        return notificationReceiptRepository.findByUserEmailIgnoreCaseOrderByReceivedAtDesc(current.getEmail())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public NotificationDtos.NotificationResponse markAsRead(UUID receiptId) {
        User current = currentUserService.currentUser();
        NotificationReceipt receipt = notificationReceiptRepository.findByIdAndUserEmailIgnoreCase(receiptId, current.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        receipt.markAsRead();
        return toResponse(notificationReceiptRepository.save(receipt));
    }

    @Transactional
    public NotificationDtos.NotificationSettingsResponse getCurrentSettings() {
        User current = currentUserService.currentUser();
        return toConfigResponse(getOrInitializeSettings(current));
    }

    @Transactional
    public NotificationDtos.NotificationSettingsResponse updateCurrentSettings(
            NotificationDtos.UpdateNotificationSettingsRequest request) {
        User current = currentUserService.currentUser();
        NotificationSettings settings = getOrInitializeSettings(current);
        settings.setDelayAlert(newOrCurrent(request.delayAlert(), settings.getDelayAlert()));
        settings.setRouteChangeAlert(newOrCurrent(request.routeChangeAlert(), settings.getRouteChangeAlert()));
        settings.setArrivalAlert(newOrCurrent(request.arrivalAlert(), settings.getArrivalAlert()));
        settings.setInactivityAlert(newOrCurrent(request.inactivityAlert(), settings.getInactivityAlert()));
        settings.setPushChannel(newOrCurrent(request.pushChannel(), settings.getPushChannel()));
        settings.setEmailChannel(newOrCurrent(request.emailChannel(), settings.getEmailChannel()));
        settings.setSmsChannel(newOrCurrent(request.smsChannel(), settings.getSmsChannel()));
        return toConfigResponse(notificationSettingsRepository.save(settings));
    }

    @Transactional
    public void createForUser(Trip trip, User user, String eventType, String message) {
        createForUser(trip, null, user, eventType, message);
    }

    @Transactional
    public void createForUser(
            Trip trip,
            SafeZone safeZone,
            User user,
            String eventType,
            String message) {
        Notification notification = new Notification();
        notification.setTrip(trip);
        notification.setSafeZone(safeZone);
        notification.setEventType(eventType);
        notification.setMessage(message);
        notification.setWasSent(false);
        notification = notificationRepository.save(notification);

        NotificationReceipt receipt = new NotificationReceipt();
        receipt.setNotification(notification);
        receipt.setUser(user);
        notificationReceiptRepository.save(receipt);

        NotificationSettings settings = getOrInitializeSettings(user);
        boolean sent = false;
        if (Boolean.TRUE.equals(settings.getPushChannel())) {
            sent = expoPushService.send(user, notification) || sent;
        }
        if (Boolean.TRUE.equals(settings.getEmailChannel())) {
            sent = emailService.enviarNotification(
                    user.getEmail(),
                    title(eventType),
                    message) || sent;
        }
        if (Boolean.TRUE.equals(settings.getSmsChannel())) {
            sent = smsService.sendToPhones(smsPhones(trip, user), message) || sent;
        }
        if (sent) {
            notification.setWasSent(true);
            notificationRepository.save(notification);
        }
    }

    private NotificationSettings getOrInitializeSettings(User user) {
        return notificationSettingsRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    NotificationSettings settings = new NotificationSettings();
                    settings.setUser(user);
                    return notificationSettingsRepository.save(settings);
                });
    }

    private Boolean newOrCurrent(Boolean newValue, Boolean currentValue) {
        return newValue == null ? currentValue : newValue;
    }

    private NotificationDtos.NotificationSettingsResponse toConfigResponse(NotificationSettings settings) {
        return new NotificationDtos.NotificationSettingsResponse(
                settings.getDelayAlert(),
                settings.getRouteChangeAlert(),
                settings.getArrivalAlert(),
                settings.getInactivityAlert(),
                settings.getPushChannel(),
                settings.getEmailChannel(),
                settings.getSmsChannel(),
                smsService.isAvailable());
    }

    private String title(String eventType) {
        return switch (eventType) {
            case NotificationEventTypes.ROUTE_DEVIATION -> "Route deviation";
            case NotificationEventTypes.SAFE_ZONE_ENTRY -> "Safe zone entry";
            case NotificationEventTypes.SAFE_ZONE_EXIT -> "Safe zone exit";
            case NotificationEventTypes.TRIP_STARTED -> "Trip started";
            case NotificationEventTypes.DELAY -> "Trip delay";
            case NotificationEventTypes.INACTIVITY -> "Inactivity detected";
            default -> "Guardian Escolar notification";
        };
    }

    private Set<String> smsPhones(Trip trip, User user) {
        Set<String> phones = new LinkedHashSet<>();
        if (user.getPhone() != null && !user.getPhone().isBlank()) {
            phones.add(user.getPhone());
        }
        if (trip != null && trip.getStudent() != null) {
            List<EmergencyContact> contacts = emergencyContactRepository
                    .findByStudentIdAndIsActiveTrueAndDeletedAtIsNull(trip.getStudent().getId());
            contacts.stream()
                    .map(EmergencyContact::getPhone)
                    .filter(phone -> phone != null && !phone.isBlank())
                    .forEach(phones::add);
        }
        return phones;
    }

    private NotificationDtos.NotificationResponse toResponse(NotificationReceipt receipt) {
        Notification notification = receipt.getNotification();
        return new NotificationDtos.NotificationResponse(
                receipt.getId(),
                notification.getId(),
                notification.getEventType(),
                notification.getMessage(),
                notification.getEventDateTime(),
                notification.getWasSent(),
                receipt.getWasRead(),
                receipt.getReceivedAt(),
                receipt.getReadAt());
    }
}
