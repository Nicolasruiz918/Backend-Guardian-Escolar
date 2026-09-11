package com.guardianescolar.api.modules.audit.repository;

import com.guardianescolar.api.modules.audit.domain.ErrorLog;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ErrorLogRepository extends JpaRepository<ErrorLog, UUID> {

    List<ErrorLog> findByOrderByCreatedAtDesc(Pageable pageable);
}
