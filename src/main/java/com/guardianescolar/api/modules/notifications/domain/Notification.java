package com.guardianescolar.api.modules.notifications.domain;

import com.guardianescolar.api.modules.trips.domain.Trip;
import com.guardianescolar.api.modules.zones.domain.SafeZone;
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

@Entity
@Table(name = "notifications")
@Getter
@Setter
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id")
    private Trip trip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "safe_zone_id")
    private SafeZone safeZone;

    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "event_at")
    private OffsetDateTime eventDateTime = OffsetDateTime.now();

    @Column(name = "was_sent")
    private Boolean wasSent = false;

}
