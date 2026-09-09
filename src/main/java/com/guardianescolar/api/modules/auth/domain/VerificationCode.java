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
@Table(name = "verification_codes")
public class VerificationCode extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private VerificationCodeType type;

    @Column(nullable = false)
    private String contact;

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

    @Column(name = "reset_token_hash", length = 128)
    private String resetTokenHash;

    @Column(name = "reset_token_expires_at")
    private Instant resetTokenExpiresAt;

    protected VerificationCode() {
    }

    public VerificationCode(UserAccount user, VerificationCodeType type, String contact, String codeHash,
            Instant expiresAt, int maxAttempts) {
        this.user = user;
        this.type = type;
        this.contact = contact;
        this.codeHash = codeHash;
        this.expiresAt = expiresAt;
        this.maxAttempts = maxAttempts;
    }

    public UserAccount getUser() {
        return user;
    }

    public String getCodeHash() {
        return codeHash;
    }

    public String getResetTokenHash() {
        return resetTokenHash;
    }

    public boolean canVerify(Instant now) {
        return usedAt == null && expiresAt.isAfter(now) && attempts < maxAttempts;
    }

    public void registerAttempt() {
        attempts++;
    }

    public void markUsed(String resetTokenHash, Instant resetTokenExpiresAt, Instant now) {
        this.usedAt = now;
        this.resetTokenHash = resetTokenHash;
        this.resetTokenExpiresAt = resetTokenExpiresAt;
    }

    public boolean canReset(Instant now) {
        return usedAt != null && resetTokenHash != null && resetTokenExpiresAt != null && resetTokenExpiresAt.isAfter(now);
    }
}
