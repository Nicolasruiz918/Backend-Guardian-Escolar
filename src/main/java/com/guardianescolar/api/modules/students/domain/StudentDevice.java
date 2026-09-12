package com.guardianescolar.api.modules.students.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "student_devices")
@Getter
@Setter
public class StudentDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "device_identifier", nullable = false, unique = true, length = 160)
    private String deviceIdentifier;

    @Column(name = "platform", nullable = false, length = 20)
    private String platform;

    @Column(name = "device_name", length = 120)
    private String deviceName;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "linked_at")
    private OffsetDateTime linkedAt;

    @Column(name = "last_used_at")
    private OffsetDateTime lastUsedAt;

    @CreationTimestamp
    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;
}
