package com.guardianescolar.api.modules.zones.repository;

import com.guardianescolar.api.modules.zones.domain.SafeZone;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SafeZoneRepository extends JpaRepository<SafeZone, UUID> {

    List<SafeZone> findAllByStudentIdOrderByNameAsc(UUID studentId);

    List<SafeZone> findAllByStudentIdAndActiveTrue(UUID studentId);

    Optional<SafeZone> findByIdAndStudentOwnerId(UUID zoneId, UUID ownerId);
}
