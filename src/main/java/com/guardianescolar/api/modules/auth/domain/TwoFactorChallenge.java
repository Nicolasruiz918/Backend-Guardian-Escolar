package com.guardianescolar.api.modules.auth.domain;

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
@Table(name = "two_factor_challenges")
public class TwoFactorChallenge extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TwoFactorMethod method;

    @Column(name = "code_hash", nullable = false, length = 128)
    private String codeHash;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private int attempts;

    @Column(name = "max_attempts", nullable = false)
    private int maxAttempts;

    @Column(name = "used_at")
    private Instant usedAt;

    protected TwoFactorChallenge() {
    }

    public TwoFactorChallenge(UserAccount user, TwoFactorMethod method, String codeHash, Instant expiresAt,
            int maxAttempts) {
        this.user = user;
        this.method = method;
        this.codeHash = codeHash;
        this.expiresAt = expiresAt;
        this.maxAttempts = maxAttempts;
    }

    public UserAccount getUser() {
        return user;
    }

    public TwoFactorMethod getMethod() {
        return method;
    }

    public String getCodeHash() {
        return codeHash;
    }

    public boolean canVerify(Instant now) {
        return usedAt == null && expiresAt.isAfter(now) && attempts < maxAttempts;
    }

    public void registerAttempt() {
        attempts++;
    }

    public void markUsed(Instant now) {
        this.usedAt = now;
    }
}
