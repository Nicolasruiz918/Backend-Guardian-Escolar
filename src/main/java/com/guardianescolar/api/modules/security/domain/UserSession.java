package com.guardianescolar.api.modules.security.domain;

import java.net.InetAddress;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_sessions")
@Getter
@Setter
public class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "token", nullable = false, length = 500)
    private String token;

    @Column(name = "device_identifier", length = 120)
    private String deviceIdentifier;

    @Column(name = "device_name", length = 120)
    private String deviceName;

    @Column(name = "platform", length = 30)
    private String platform;

    @Column(name = "session_started_at")
    private OffsetDateTime sessionStartedAt = OffsetDateTime.now();

    @Column(name = "session_ended_at")
    private OffsetDateTime sessionEndedAt;

    @JdbcTypeCode(SqlTypes.INET)
    @Column(name = "source_ip", columnDefinition = "inet")
    private InetAddress sourceIp;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "session_status", columnDefinition = "session_status_enum")
    private SessionStatus sessionStatus = SessionStatus.ACTIVE;

    @Column(name = "email_confirmed")
    private Boolean emailConfirmed = true;

    @Column(name = "device_confirmation_token", unique = true, length = 255)
    private String deviceConfirmationToken;

    @Column(name = "device_confirmation_expires_at")
    private OffsetDateTime deviceConfirmationExpiresAt;

    @Column(name = "device_confirmation_return_url", length = 500)
    private String deviceConfirmationReturnUrl;

    @CreationTimestamp
    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

}
