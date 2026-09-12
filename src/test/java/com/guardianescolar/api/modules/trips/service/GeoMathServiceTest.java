package com.guardianescolar.api.modules.trips.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class GeoMathServiceTest {

    private final GeoMathService geoMathService = new GeoMathService();

    @Test
    void distanceMetersReturnsSmallDistanceForNearPoints() {
        double distance = geoMathService.distanceMeters(
                new BigDecimal("2.927300"),
                new BigDecimal("-75.281900"),
                new BigDecimal("2.927310"),
                new BigDecimal("-75.281910"));

        assertThat(distance).isPositive().isLessThan(3);
    }

    @Test
    void distanceToPolylineMetersReturnsNearZeroWhenPointIsOnSegment() {
        List<GeoMathService.GeoPoint> route = List.of(
                new GeoMathService.GeoPoint(new BigDecimal("2.927300"), new BigDecimal("-75.281900")),
                new GeoMathService.GeoPoint(new BigDecimal("2.930000"), new BigDecimal("-75.280000")));

        double distance = geoMathService.distanceToPolylineMeters(
                new BigDecimal("2.928650"),
                new BigDecimal("-75.280950"),
                route);

        assertThat(distance).isLessThan(5);
    }
}
