package com.guardianescolar.api.modules.notifications.domain;

import com.guardianescolar.api.modules.auth.domain.UserAccount;
import com.guardianescolar.api.modules.students.domain.Student;
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
@Table(name = "notifications")
public class GuardianNotification extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private GuardianEvent event;

    @Column(nullable = false, length = 160)
    private String title;

    @Column(nullable = false, length = 600)
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 60)
    private EventType type;

    @Column(name = "read_at")
    private Instant readAt;

    protected GuardianNotification() {
    }

    public GuardianNotification(UserAccount user, Student student, GuardianEvent event, String title, String body,
            EventType type) {
        this.user = user;
        this.student = student;
        this.event = event;
        this.title = title;
        this.body = body;
        this.type = type;
    }

    public UserAccount getUser() { return user; }
    public Student getStudent() { return student; }
    public GuardianEvent getEvent() { return event; }
    public String getTitle() { return title; }
    public String getBody() { return body; }
    public EventType getType() { return type; }
    public Instant getReadAt() { return readAt; }
    public void markRead(Instant at) { this.readAt = at; }
}
