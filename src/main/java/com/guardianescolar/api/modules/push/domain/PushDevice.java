package com.guardianescolar.api.modules.push.domain;

import com.guardianescolar.api.modules.auth.domain.UserAccount;
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
@Table(name = "push_devices")
public class PushDevice extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;

    @Column(nullable = false, unique = true, length = 255)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PushPlatform platform;

    @Column(name = "device_name", length = 120)
    private String deviceName;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "last_seen_at", nullable = false)
    private Instant lastSeenAt;

    protected PushDevice() {
    }

    public PushDevice(UserAccount user, String token, PushPlatform platform, String deviceName, Instant lastSeenAt) {
        this.user = user;
        this.token = token;
        this.platform = platform;
        this.deviceName = deviceName;
        this.lastSeenAt = lastSeenAt;
    }

    public UserAccount getUser() {
        return user;
    }

    public String getToken() {
        return token;
    }

    public PushPlatform getPlatform() {
        return platform;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public boolean isActive() {
        return active;
    }

    public Instant getLastSeenAt() {
        return lastSeenAt;
    }

    public void register(UserAccount user, PushPlatform platform, String deviceName, Instant lastSeenAt) {
        this.user = user;
        this.platform = platform;
        this.deviceName = deviceName;
        this.lastSeenAt = lastSeenAt;
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }
}
