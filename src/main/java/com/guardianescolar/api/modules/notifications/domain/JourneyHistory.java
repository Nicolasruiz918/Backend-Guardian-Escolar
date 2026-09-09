package com.guardianescolar.api.modules.notifications.domain;

import com.guardianescolar.api.modules.students.domain.Student;
import com.guardianescolar.api.modules.trips.domain.LocationPoint;
import com.guardianescolar.api.shared.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "journey_history")
public class JourneyHistory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private LocationPoint location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private GuardianEvent event;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Column(name = "recorded_at", nullable = false)
    private Instant recordedAt;

    @Column(nullable = false, length = 60)
    private String status;

    protected JourneyHistory() {
    }

    public JourneyHistory(Student student, LocationPoint location, GuardianEvent event, String status) {
        this.student = student;
        this.location = location;
        this.event = event;
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        this.recordedAt = location.getRecordedAt();
        this.status = status;
    }

    public Student getStudent() { return student; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public Instant getRecordedAt() { return recordedAt; }
    public String getStatus() { return status; }
}
