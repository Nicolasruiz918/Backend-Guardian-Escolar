package com.guardianescolar.api.modules.push.repository;

import com.guardianescolar.api.modules.push.domain.PushDevice;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PushDeviceRepository extends JpaRepository<PushDevice, UUID> {

    Optional<PushDevice> findByToken(String token);

    Optional<PushDevice> findByIdAndUserId(UUID id, UUID userId);

    List<PushDevice> findAllByUserIdOrderByCreatedAtDesc(UUID userId);

    List<PushDevice> findAllByUserIdAndActiveTrue(UUID userId);
}
