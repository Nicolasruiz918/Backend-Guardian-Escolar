package com.guardianescolar.api.modules.notifications.repository;

import com.guardianescolar.api.modules.notifications.domain.GuardianNotification;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuardianNotificationRepository extends JpaRepository<GuardianNotification, UUID> {

    List<GuardianNotification> findAllByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<GuardianNotification> findByIdAndUserId(UUID notificationId, UUID userId);

    long countByUserIdAndReadAtIsNull(UUID userId);
}
