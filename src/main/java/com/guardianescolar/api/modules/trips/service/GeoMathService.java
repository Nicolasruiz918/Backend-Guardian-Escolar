package com.guardianescolar.api.modules.trips.service;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class GeoMathService {

    private static final double EARTH_RADIUS_METERS = 6_371_000.0;

    public record GeoPoint(BigDecimal latitude, BigDecimal longitude) {
    }

    public double distanceMeters(BigDecimal latitudeA, BigDecimal longitudeA, BigDecimal latitudeB, BigDecimal longitudeB) {
        double lat1 = Math.toRadians(latitudeA.doubleValue());
        double lat2 = Math.toRadians(latitudeB.doubleValue());
        double deltaLat = Math.toRadians(latitudeB.doubleValue() - latitudeA.doubleValue());
        double deltaLon = Math.toRadians(longitudeB.doubleValue() - longitudeA.doubleValue());

        double haversine = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        return 2 * EARTH_RADIUS_METERS * Math.atan2(Math.sqrt(haversine), Math.sqrt(1 - haversine));
    }

    public boolean isInsideRadius(
            BigDecimal latitude,
            BigDecimal longitude,
            BigDecimal centroLatitude,
            BigDecimal centroLongitude,
            int radiusMeters) {
        return distanceMeters(latitude, longitude, centroLatitude, centroLongitude) <= radiusMeters;
    }

    public double distanceToPolylineMeters(BigDecimal latitude, BigDecimal longitude, List<GeoPoint> points) {
        if (points.size() < 2) {
            return Double.MAX_VALUE;
        }

        double minDistance = Double.MAX_VALUE;
        for (int index = 0; index < points.size() - 1; index++) {
            GeoPoint start = points.get(index);
            GeoPoint end = points.get(index + 1);
            minDistance = Math.min(minDistance, distanceToSegmentMeters(latitude, longitude, start, end));
        }
        return minDistance;
    }

    private double distanceToSegmentMeters(BigDecimal latitude, BigDecimal longitude, GeoPoint start, GeoPoint end) {
        double referenceLat = Math.toRadians(latitude.doubleValue());

        double pointX = longitudeToMeters(longitude.doubleValue(), referenceLat);
        double pointY = latitudeToMeters(latitude.doubleValue());
        double startX = longitudeToMeters(start.longitude().doubleValue(), referenceLat);
        double startY = latitudeToMeters(start.latitude().doubleValue());
        double endX = longitudeToMeters(end.longitude().doubleValue(), referenceLat);
        double endY = latitudeToMeters(end.latitude().doubleValue());

        double deltaX = endX - startX;
        double deltaY = endY - startY;
        double lengthSquared = deltaX * deltaX + deltaY * deltaY;

        if (lengthSquared == 0) {
            return Math.hypot(pointX - startX, pointY - startY);
        }

        double projection = ((pointX - startX) * deltaX + (pointY - startY) * deltaY) / lengthSquared;
        double clampedProjection = Math.max(0, Math.min(1, projection));
        double closestX = startX + clampedProjection * deltaX;
        double closestY = startY + clampedProjection * deltaY;
        return Math.hypot(pointX - closestX, pointY - closestY);
    }

    private double latitudeToMeters(double latitude) {
        return Math.toRadians(latitude) * EARTH_RADIUS_METERS;
    }

    private double longitudeToMeters(double longitude, double referenceLat) {
        return Math.toRadians(longitude) * EARTH_RADIUS_METERS * Math.cos(referenceLat);
    }
}
