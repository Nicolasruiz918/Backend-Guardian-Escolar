package com.guardianescolar.api.modules.zones.repository;

import com.guardianescolar.api.modules.zones.domain.SafeZone;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SafeZoneRepository extends JpaRepository<SafeZone, UUID> {

    List<SafeZone> findByIsActiveTrueAndDeletedAtIsNullOrderByZoneNameAsc();

    List<SafeZone> findByStudentUserEmailIgnoreCaseAndIsActiveTrueAndDeletedAtIsNullOrderByZoneNameAsc(String email);

    List<SafeZone> findByStudentIdAndIsActiveTrueAndDeletedAtIsNull(UUID studentId);

    Optional<SafeZone> findByIdAndDeletedAtIsNull(UUID id);
}
