package com.guardianescolar.api.modules.students.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StudentRequest(
        @NotBlank @Size(max = 160) String fullName,
        @Size(max = 80) String grade,
        @Min(1) @Max(25) Integer age,
        @Size(max = 160) String school,
        @Size(max = 500) String avatarUrl,
        @Size(max = 160) String emergencyContactName,
        @Size(max = 40) String emergencyContactPhone,
        Boolean active) {
}
