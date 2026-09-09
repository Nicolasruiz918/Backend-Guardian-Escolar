package com.guardianescolar.api.modules.notifications.repository;

import com.guardianescolar.api.modules.notifications.domain.GuardianEvent;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuardianEventRepository extends JpaRepository<GuardianEvent, UUID> {

    long countByStudentOwnerId(UUID ownerId);

    List<GuardianEvent> findTop50ByStudentOwnerIdOrderByOccurredAtDesc(UUID ownerId);
}
