package com.guardianescolar.api.modules.auth.domain;

import com.guardianescolar.api.shared.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_preferences")
public class UserPreference extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserAccount user;

    @Column(nullable = false, length = 10)
    private String language = "es";

    @Column(nullable = false, length = 60)
    private String timezone = "America/Bogota";

    @Column(name = "push_enabled", nullable = false)
    private boolean pushEnabled = true;

    @Column(name = "email_enabled", nullable = false)
    private boolean emailEnabled = true;

    @Column(name = "sms_enabled", nullable = false)
    private boolean smsEnabled;

    @Column(name = "location_alerts_enabled", nullable = false)
    private boolean locationAlertsEnabled = true;

    @Column(name = "route_alerts_enabled", nullable = false)
    private boolean routeAlertsEnabled = true;

    @Column(name = "safe_zone_alerts_enabled", nullable = false)
    private boolean safeZoneAlertsEnabled = true;

    protected UserPreference() {
    }

    public UserPreference(UserAccount user) {
        this.user = user;
    }

    public UserAccount getUser() {
        return user;
    }

    public String getLanguage() {
        return language;
    }

    public String getTimezone() {
        return timezone;
    }

    public boolean isPushEnabled() {
        return pushEnabled;
    }

    public boolean isEmailEnabled() {
        return emailEnabled;
    }

    public boolean isSmsEnabled() {
        return smsEnabled;
    }

    public boolean isLocationAlertsEnabled() {
        return locationAlertsEnabled;
    }

    public boolean isRouteAlertsEnabled() {
        return routeAlertsEnabled;
    }

    public boolean isSafeZoneAlertsEnabled() {
        return safeZoneAlertsEnabled;
    }

    public void update(String language, String timezone, boolean pushEnabled, boolean emailEnabled, boolean smsEnabled,
            boolean locationAlertsEnabled, boolean routeAlertsEnabled, boolean safeZoneAlertsEnabled) {
        this.language = language;
        this.timezone = timezone;
        this.pushEnabled = pushEnabled;
        this.emailEnabled = emailEnabled;
        this.smsEnabled = smsEnabled;
        this.locationAlertsEnabled = locationAlertsEnabled;
        this.routeAlertsEnabled = routeAlertsEnabled;
        this.safeZoneAlertsEnabled = safeZoneAlertsEnabled;
    }
}
