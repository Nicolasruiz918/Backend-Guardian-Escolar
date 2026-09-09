package com.guardianescolar.api.modules.routes.repository;

import com.guardianescolar.api.modules.routes.domain.SchoolRoute;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchoolRouteRepository extends JpaRepository<SchoolRoute, UUID> {

    List<SchoolRoute> findAllByStudentIdOrderByNameAsc(UUID studentId);

    Optional<SchoolRoute> findByIdAndStudentOwnerId(UUID routeId, UUID ownerId);
}
