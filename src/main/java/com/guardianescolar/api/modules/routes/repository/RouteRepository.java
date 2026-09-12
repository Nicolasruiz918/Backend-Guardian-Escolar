package com.guardianescolar.api.modules.routes.repository;

import com.guardianescolar.api.modules.routes.domain.Route;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteRepository extends JpaRepository<Route, UUID> {

    List<Route> findByDeletedAtIsNullOrderByRouteNameAsc();

    List<Route> findByCreatedByIdAndDeletedAtIsNullOrderByRouteNameAsc(UUID createdById);

    Optional<Route> findByIdAndDeletedAtIsNull(UUID id);
}
