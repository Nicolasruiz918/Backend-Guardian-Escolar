package com.guardianescolar.api.modules.routes.service;

import com.guardianescolar.api.modules.auth.service.CurrentUserService;
import com.guardianescolar.api.modules.routes.domain.Stop;
import com.guardianescolar.api.modules.routes.domain.Route;
import com.guardianescolar.api.modules.routes.dto.RouteDtos;
import com.guardianescolar.api.modules.routes.repository.StopRepository;
import com.guardianescolar.api.modules.routes.repository.RouteRepository;
import com.guardianescolar.api.modules.security.domain.User;
import com.guardianescolar.api.shared.exception.ResourceNotFoundException;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RouteService {

    private final RouteRepository routeRepository;
    private final StopRepository stopRepository;
    private final CurrentUserService currentUserService;

    @Transactional(readOnly = true)
    public List<RouteDtos.RouteResponse> list() {
        User current = currentUserService.currentUser();
        List<Route> routes = currentUserService.isAdmin(current)
                ? routeRepository.findByDeletedAtIsNullOrderByRouteNameAsc()
                : routeRepository.findByCreatedByIdAndDeletedAtIsNullOrderByRouteNameAsc(current.getId());
        return routes.stream()
                .map(route -> toResponse(route, stopRepository.findByRouteIdOrderByStopOrderAsc(route.getId())))
                .toList();
    }

    @Transactional
    public RouteDtos.RouteResponse create(RouteDtos.RouteRequest request) {
        User current = currentUserService.currentUser();
        validateRequest(request);
        Route route = new Route();
        applyData(route, request);
        route.setCreatedBy(current);
        Route saved = routeRepository.save(route);
        List<Stop> stops = saveStops(saved, request.stops());
        return toResponse(saved, stops);
    }

    @Transactional
    public RouteDtos.RouteResponse update(UUID routeId, RouteDtos.RouteRequest request) {
        User current = currentUserService.currentUser();
        Route route = routeRepository.findByIdAndDeletedAtIsNull(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found"));
        validateRouteAccess(route, current);
        validateRequest(request);
        applyData(route, request);
        route.setUpdatedBy(current);
        Route saved = routeRepository.save(route);
        List<Stop> stops;
        if (request.stops() == null) {
            stops = stopRepository.findByRouteIdOrderByStopOrderAsc(saved.getId());
        } else {
            stopRepository.deleteByRouteId(saved.getId());
            stops = saveStops(saved, request.stops());
        }
        return toResponse(saved, stops);
    }

    @Transactional
    public void delete(UUID routeId) {
        User current = currentUserService.currentUser();
        Route route = routeRepository.findByIdAndDeletedAtIsNull(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found"));
        validateRouteAccess(route, current);
        route.setDeletedAt(OffsetDateTime.now());
        route.setUpdatedBy(current);
        routeRepository.save(route);
    }

    @Transactional
    public RouteDtos.StopResponse addStop(UUID routeId, RouteDtos.StopRequest request) {
        User current = currentUserService.currentUser();
        Route route = routeRepository.findByIdAndDeletedAtIsNull(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found"));
        validateRouteAccess(route, current);
        Stop stop = createStop(route, request);
        return toStopResponse(stopRepository.save(stop));
    }

    @Transactional(readOnly = true)
    public Route getAllowed(UUID routeId) {
        Route route = routeRepository.findByIdAndDeletedAtIsNull(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found"));
        validateRouteAccess(route, currentUserService.currentUser());
        return route;
    }

    private void applyData(Route route, RouteDtos.RouteRequest request) {
        route.setRouteName(request.routeName().trim());
        route.setDescription(normalizeOptionalText(request.description()));
        route.setOriginLatitude(request.originLatitude());
        route.setOriginLongitude(request.originLongitude());
        route.setDestinationLatitude(request.destinationLatitude());
        route.setDestinationLongitude(request.destinationLongitude());
    }

    private List<Stop> saveStops(Route route, List<RouteDtos.StopRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return List.of();
        }
        List<Stop> stops = requests.stream()
                .map(request -> createStop(route, request))
                .toList();
        return stopRepository.saveAll(stops).stream()
                .sorted(java.util.Comparator.comparing(Stop::getStopOrder))
                .toList();
    }

    private Stop createStop(Route route, RouteDtos.StopRequest request) {
        Stop stop = new Stop();
        stop.setRoute(route);
        stop.setStopOrder(request.stopOrder());
        stop.setStopName(request.stopName().trim());
        stop.setLatitude(request.latitude());
        stop.setLongitude(request.longitude());
        return stop;
    }

    private void validateRequest(RouteDtos.RouteRequest request) {
        validateCoordinatePair("origin", request.originLatitude(), request.originLongitude());
        validateCoordinatePair("destination", request.destinationLatitude(), request.destinationLongitude());
        if (request.stops() == null || request.stops().isEmpty()) {
            return;
        }
        Set<Integer> orders = new HashSet<>();
        for (RouteDtos.StopRequest stop : request.stops()) {
            if (!orders.add(stop.stopOrder())) {
                throw new IllegalArgumentException("Stops cannot repeat the same order");
            }
        }
    }

    private void validateCoordinatePair(String name, BigDecimal latitude, BigDecimal longitude) {
        if ((latitude == null) != (longitude == null)) {
            throw new IllegalArgumentException("The " + name + " coordinate must include latitude and longitude");
        }
    }

    private String normalizeOptionalText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private RouteDtos.RouteResponse toResponse(Route route, List<Stop> stops) {
        return new RouteDtos.RouteResponse(
                route.getId(),
                route.getRouteName(),
                route.getDescription(),
                route.getOriginLatitude(),
                route.getOriginLongitude(),
                route.getDestinationLatitude(),
                route.getDestinationLongitude(),
                stops.stream().map(this::toStopResponse).toList());
    }

    private RouteDtos.StopResponse toStopResponse(Stop stop) {
        return new RouteDtos.StopResponse(
                stop.getId(),
                stop.getStopOrder(),
                stop.getStopName(),
                stop.getLatitude(),
                stop.getLongitude());
    }

    private void validateRouteAccess(Route route, User current) {
        if (currentUserService.isAdmin(current) || route.getCreatedBy() == null) {
            return;
        }
        if (!route.getCreatedBy().getId().equals(current.getId())) {
            throw new AccessDeniedException("You do not have access to this route");
        }
    }
}
