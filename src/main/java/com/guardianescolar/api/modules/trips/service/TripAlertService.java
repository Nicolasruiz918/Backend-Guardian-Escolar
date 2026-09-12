package com.guardianescolar.api.modules.trips.service;

import com.guardianescolar.api.modules.notifications.service.NotificationService;
import com.guardianescolar.api.modules.notifications.domain.NotificationEventTypes;
import com.guardianescolar.api.modules.routes.domain.Route;
import com.guardianescolar.api.modules.routes.domain.Stop;
import com.guardianescolar.api.modules.routes.repository.StopRepository;
import com.guardianescolar.api.modules.trips.domain.Coordinate;
import com.guardianescolar.api.modules.trips.domain.Trip;
import com.guardianescolar.api.modules.trips.repository.CoordinateRepository;
import com.guardianescolar.api.modules.trips.repository.TripRepository;
import com.guardianescolar.api.modules.zones.domain.SafeZone;
import com.guardianescolar.api.modules.zones.repository.SafeZoneRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TripAlertService {

    private static final int DEFAULT_INACTIVITY_THRESHOLD_SECONDS = 300;

    private final CoordinateRepository coordinateRepository;
    private final TripRepository tripRepository;
    private final SafeZoneRepository safeZoneRepository;
    private final StopRepository stopRepository;
    private final NotificationService notificationService;
    private final GeoMathService geoMathService;

    @Value("${guardian.gps.route-deviation-threshold-meters:150}")
    private double routeDeviationThresholdMeters;

    public void processCoordinate(Trip trip, Coordinate coordinate) {
        detectInactivity(trip, coordinate);
        detectSafeZones(trip, coordinate);
        detectRouteDeviation(trip, coordinate);
    }

    private void detectInactivity(Trip trip, Coordinate coordinate) {
        int stoppedSeconds = coordinate.getStoppedSeconds() == null ? 0 : coordinate.getStoppedSeconds();
        int threshold = safeZoneRepository.findByStudentIdAndIsActiveTrueAndDeletedAtIsNull(trip.getStudent().getId()).stream()
                .map(SafeZone::getInactivityAlertSeconds)
                .min(Comparator.naturalOrder())
                .orElse(DEFAULT_INACTIVITY_THRESHOLD_SECONDS);
        if (stoppedSeconds >= threshold) {
            notificationService.createForUser(
                    trip,
                    trip.getStudent().getUser(),
                    NotificationEventTypes.INACTIVITY,
                    "Long inactivity detected during " + trip.getStudent().getFullName() + "'s trip");
        }
    }

    private void detectSafeZones(Trip trip, Coordinate coordinate) {
        List<SafeZone> zones = safeZoneRepository
                .findByStudentIdAndIsActiveTrueAndDeletedAtIsNull(trip.getStudent().getId());
        if (zones.isEmpty()) {
            return;
        }

        Coordinate previous = coordinateRepository.findTop2ByTripIdOrderByRecordedAtDesc(trip.getId()).stream()
                .filter(recorded -> !recorded.getId().equals(coordinate.getId()))
                .findFirst()
                .orElse(null);

        for (SafeZone zone : zones) {
            notifySafeZoneTransition(trip, coordinate, previous, zone);
        }
    }

    private void notifySafeZoneTransition(Trip trip, Coordinate coordinate, Coordinate previous, SafeZone zone) {
        boolean insideNow = isInsideZone(coordinate, zone);
        boolean insideBefore = previous != null && isInsideZone(previous, zone);
        if (insideNow && !insideBefore) {
            notificationService.createForUser(
                    trip,
                    zone,
                    trip.getStudent().getUser(),
                    NotificationEventTypes.SAFE_ZONE_ENTRY,
                    trip.getStudent().getFullName() + " entered safe zone " + zone.getZoneName());
        } else if (!insideNow && insideBefore) {
            notificationService.createForUser(
                    trip,
                    zone,
                    trip.getStudent().getUser(),
                    NotificationEventTypes.SAFE_ZONE_EXIT,
                    trip.getStudent().getFullName() + " left safe zone " + zone.getZoneName());
        }
    }

    private void detectRouteDeviation(Trip trip, Coordinate coordinate) {
        if (Boolean.TRUE.equals(trip.getHadDeviation())) {
            return;
        }

        List<GeoMathService.GeoPoint> route = routePoints(trip.getRoute());
        if (route.size() < 2) {
            return;
        }

        double routeDistance = geoMathService.distanceToPolylineMeters(
                coordinate.getLatitude(),
                coordinate.getLongitude(),
                route);
        if (routeDistance > routeDeviationThresholdMeters) {
            trip.setHadDeviation(true);
            tripRepository.save(trip);
            notificationService.createForUser(
                    trip,
                    trip.getStudent().getUser(),
                    NotificationEventTypes.ROUTE_DEVIATION,
                    "Possible route deviation detected for " + trip.getStudent().getFullName());
        }
    }

    private boolean isInsideZone(Coordinate coordinate, SafeZone zone) {
        return geoMathService.isInsideRadius(
                coordinate.getLatitude(),
                coordinate.getLongitude(),
                zone.getLatitude(),
                zone.getLongitude(),
                zone.getRadiusMeters());
    }

    private List<GeoMathService.GeoPoint> routePoints(Route route) {
        List<GeoMathService.GeoPoint> points = new ArrayList<>();
        if (route.getOriginLatitude() != null && route.getOriginLongitude() != null) {
            points.add(new GeoMathService.GeoPoint(route.getOriginLatitude(), route.getOriginLongitude()));
        }

        List<Stop> stops = stopRepository.findByRouteIdOrderByStopOrderAsc(route.getId());
        points.addAll(stops.stream()
                .map(stop -> new GeoMathService.GeoPoint(stop.getLatitude(), stop.getLongitude()))
                .toList());

        if (route.getDestinationLatitude() != null && route.getDestinationLongitude() != null) {
            points.add(new GeoMathService.GeoPoint(route.getDestinationLatitude(), route.getDestinationLongitude()));
        }
        return points;
    }
}
