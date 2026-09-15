package com.guardianescolar.api.modules.audit.repository;

import com.guardianescolar.api.modules.audit.domain.ActivityLog;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, UUID> {
}
