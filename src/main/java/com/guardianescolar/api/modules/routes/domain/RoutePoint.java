package com.guardianescolar.api.modules.routes.domain;

import com.guardianescolar.api.shared.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "route_points")
public class RoutePoint extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private SchoolRoute route;

    @Column(name = "sequence_number", nullable = false)
    private int sequenceNumber;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    protected RoutePoint() {
    }

    public RoutePoint(SchoolRoute route, int sequenceNumber, double latitude, double longitude) {
        this.route = route;
        this.sequenceNumber = sequenceNumber;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getSequenceNumber() { return sequenceNumber; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
}
