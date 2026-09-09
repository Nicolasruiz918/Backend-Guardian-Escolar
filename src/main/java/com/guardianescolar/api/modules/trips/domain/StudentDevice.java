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
@Table(name = "student_devices")
public class StudentDevice extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, unique = true, length = 120)
    private String identifier;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "last_seen_at")
    private Instant lastSeenAt;

    protected StudentDevice() {
    }

    public StudentDevice(Student student, String name, String identifier) {
        this.student = student;
        this.name = name;
        this.identifier = identifier;
    }

    public Student getStudent() {
        return student;
    }

    public String getName() {
        return name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public boolean isActive() {
        return active;
    }

    public Instant getLastSeenAt() {
        return lastSeenAt;
    }

    public void update(String name, boolean active) {
        this.name = name;
        this.active = active;
    }

    public void markSeen(Instant at) {
        this.lastSeenAt = at;
    }
}
