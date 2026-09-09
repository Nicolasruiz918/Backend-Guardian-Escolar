package com.guardianescolar.api.modules.notifications.repository;

import com.guardianescolar.api.modules.notifications.domain.JourneyHistory;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JourneyHistoryRepository extends JpaRepository<JourneyHistory, UUID> {

    List<JourneyHistory> findTop100ByStudentIdOrderByRecordedAtDesc(UUID studentId);

    List<JourneyHistory> findTop200ByStudentOwnerIdOrderByRecordedAtDesc(UUID ownerId);
}
