package com.guardianescolar.api.modules.notifications.domain;

import com.guardianescolar.api.modules.students.domain.Student;
import com.guardianescolar.api.modules.trips.domain.LocationPoint;
import com.guardianescolar.api.shared.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "guardian_events")
public class GuardianEvent extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private LocationPoint location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 60)
    private EventType type;

    @Column(nullable = false, length = 500)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EventSeverity severity;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    protected GuardianEvent() {
    }

    public GuardianEvent(Student student, LocationPoint location, EventType type, String message, EventSeverity severity,
            Instant occurredAt) {
        this.student = student;
        this.location = location;
        this.type = type;
        this.message = message;
        this.severity = severity;
        this.occurredAt = occurredAt;
    }

    public Student getStudent() { return student; }
    public LocationPoint getLocation() { return location; }
    public EventType getType() { return type; }
    public String getMessage() { return message; }
    public EventSeverity getSeverity() { return severity; }
    public Instant getOccurredAt() { return occurredAt; }
}
