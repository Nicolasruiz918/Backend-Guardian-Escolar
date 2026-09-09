package com.guardianescolar.api.modules.students.domain;

import com.guardianescolar.api.modules.auth.domain.UserAccount;
import com.guardianescolar.api.shared.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "students")
public class Student extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount owner;

    @Column(name = "full_name", nullable = false, length = 160)
    private String fullName;

    @Column(length = 80)
    private String grade;

    private Integer age;

    @Column(length = 160)
    private String school;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(name = "emergency_contact_name", length = 160)
    private String emergencyContactName;

    @Column(name = "emergency_contact_phone", length = 40)
    private String emergencyContactPhone;

    @Column(nullable = false)
    private boolean active = true;

    protected Student() {
    }

    public Student(UserAccount owner, String fullName) {
        this.owner = owner;
        this.fullName = fullName;
    }

    public UserAccount getOwner() {
        return owner;
    }

    public String getFullName() {
        return fullName;
    }

    public String getGrade() {
        return grade;
    }

    public Integer getAge() {
        return age;
    }

    public String getSchool() {
        return school;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getEmergencyContactName() {
        return emergencyContactName;
    }

    public String getEmergencyContactPhone() {
        return emergencyContactPhone;
    }

    public boolean isActive() {
        return active;
    }

    public void update(String fullName, String grade, Integer age, String school, String avatarUrl,
            String emergencyContactName, String emergencyContactPhone, boolean active) {
        this.fullName = fullName;
        this.grade = grade;
        this.age = age;
        this.school = school;
        this.avatarUrl = avatarUrl;
        this.emergencyContactName = emergencyContactName;
        this.emergencyContactPhone = emergencyContactPhone;
        this.active = active;
    }
}
