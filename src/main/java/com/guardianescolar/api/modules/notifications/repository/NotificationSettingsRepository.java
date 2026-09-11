package com.guardianescolar.api.modules.notifications.repository;

import com.guardianescolar.api.modules.notifications.domain.NotificationSettings;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationSettingsRepository extends JpaRepository<NotificationSettings, UUID> {
    Optional<NotificationSettings> findByUserId(UUID UserId);
}
