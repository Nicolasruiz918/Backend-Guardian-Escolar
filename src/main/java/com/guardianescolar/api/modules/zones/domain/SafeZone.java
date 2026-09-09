package com.guardianescolar.api.modules.zones.domain;

import com.guardianescolar.api.modules.students.domain.Student;
import com.guardianescolar.api.shared.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "safe_zones")
public class SafeZone extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(name = "center_latitude", nullable = false)
    private double centerLatitude;

    @Column(name = "center_longitude", nullable = false)
    private double centerLongitude;

    @Column(name = "radius_meters", nullable = false)
    private double radiusMeters;

    @Column(nullable = false)
    private boolean active = true;

    protected SafeZone() {
    }

    public SafeZone(Student student, String name) {
        this.student = student;
        this.name = name;
    }

    public Student getStudent() { return student; }
    public String getName() { return name; }
    public double getCenterLatitude() { return centerLatitude; }
    public double getCenterLongitude() { return centerLongitude; }
    public double getRadiusMeters() { return radiusMeters; }
    public boolean isActive() { return active; }

    public void update(String name, double centerLatitude, double centerLongitude, double radiusMeters, boolean active) {
        this.name = name;
        this.centerLatitude = centerLatitude;
        this.centerLongitude = centerLongitude;
        this.radiusMeters = radiusMeters;
        this.active = active;
    }
}
