package com.guardianescolar.api.modules.auth.domain;

import com.guardianescolar.api.shared.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class UserAccount extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "full_name", nullable = false, length = 160)
    private String fullName;

    @Column(length = 40)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private UserRole role = UserRole.PARENT;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(name = "two_factor_enabled", nullable = false)
    private boolean twoFactorEnabled;

    @Enumerated(EnumType.STRING)
    @Column(name = "two_factor_method", nullable = false, length = 20)
    private TwoFactorMethod twoFactorMethod = TwoFactorMethod.EMAIL;

    protected UserAccount() {
    }

    public UserAccount(String email, String passwordHash, String fullName, String phone) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public UserRole getRole() {
        return role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isTwoFactorEnabled() {
        return twoFactorEnabled;
    }

    public TwoFactorMethod getTwoFactorMethod() {
        return twoFactorMethod;
    }

    public void changePassword(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void configureTwoFactor(boolean enabled, TwoFactorMethod method) {
        this.twoFactorEnabled = enabled;
        this.twoFactorMethod = method == null ? TwoFactorMethod.EMAIL : method;
    }
}
