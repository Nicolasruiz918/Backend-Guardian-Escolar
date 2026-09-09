package com.guardianescolar.api.modules.trips.domain;

import com.guardianescolar.api.modules.students.domain.Student;
import com.guardianescolar.api.shared.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "locations")
public class LocationPoint extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id")
    private StudentDevice device;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    private Double accuracy;
    private Double speed;

    @Column(name = "battery_level")
    private Integer batteryLevel;

    @Column(name = "recorded_at", nullable = false)
    private Instant recordedAt;

    protected LocationPoint() {
    }

    public LocationPoint(Student student, StudentDevice device, double latitude, double longitude, Double accuracy,
            Double speed, Integer batteryLevel, Instant recordedAt) {
        this.student = student;
        this.device = device;
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
        this.speed = speed;
        this.batteryLevel = batteryLevel;
        this.recordedAt = recordedAt;
    }

    public Student getStudent() {
        return student;
    }

    public StudentDevice getDevice() {
        return device;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public Double getAccuracy() {
        return accuracy;
    }

    public Double getSpeed() {
        return speed;
    }

    public Integer getBatteryLevel() {
        return batteryLevel;
    }

    public Instant getRecordedAt() {
        return recordedAt;
    }
}
