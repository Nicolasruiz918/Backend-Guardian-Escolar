package com.guardianescolar.api.modules.trips.repository;

import com.guardianescolar.api.modules.trips.domain.LocationPoint;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationPointRepository extends JpaRepository<LocationPoint, UUID> {

    Optional<LocationPoint> findTopByStudentIdOrderByRecordedAtDesc(UUID studentId);

    List<LocationPoint> findTop50ByStudentIdOrderByRecordedAtDesc(UUID studentId);

    Optional<LocationPoint> findTopByStudentOwnerIdOrderByRecordedAtDesc(UUID ownerId);

    long countByStudentOwnerId(UUID ownerId);
}
