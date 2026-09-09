package com.guardianescolar.api.modules.students.dto;

import java.util.UUID;

public record StudentResponse(UUID id, String fullName, String grade, Integer age, String school, String avatarUrl,
        String emergencyContactName, String emergencyContactPhone, boolean active) {
}
