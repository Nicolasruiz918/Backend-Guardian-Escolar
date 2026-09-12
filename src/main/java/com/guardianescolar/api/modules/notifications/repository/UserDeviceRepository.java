package com.guardianescolar.api.modules.notifications.repository;

import com.guardianescolar.api.modules.notifications.domain.UserDevice;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDeviceRepository extends JpaRepository<UserDevice, UUID> {

    List<UserDevice> findByUserIdAndIsActiveTrueAndDeletedAtIsNull(UUID UserId);

    Optional<UserDevice> findByExpoPushToken(String expoPushToken);

    Optional<UserDevice> findByIdAndUserId(UUID id, UUID UserId);
}
