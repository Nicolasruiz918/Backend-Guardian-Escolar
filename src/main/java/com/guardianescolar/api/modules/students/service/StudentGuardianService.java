package com.guardianescolar.api.modules.students.service;

import com.guardianescolar.api.modules.security.domain.User;
import com.guardianescolar.api.modules.security.repository.UserRepository;
import com.guardianescolar.api.modules.students.domain.Student;
import com.guardianescolar.api.modules.students.domain.StudentGuardian;
import com.guardianescolar.api.modules.students.dto.StudentDtos;
import com.guardianescolar.api.modules.students.repository.StudentGuardianRepository;
import com.guardianescolar.api.shared.exception.ResourceNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentGuardianService {

    private static final String STATUS_ACTIVE = "ACTIVE";
    private static final String RELATIONSHIP_OWNER = "OWNER";
    private static final String RELATIONSHIP_GUARDIAN = "GUARDIAN";
    private static final String RELATIONSHIP_VIEWER = "VIEWER";

    private final UserRepository userRepository;
    private final StudentGuardianRepository studentGuardianRepository;
    private final StudentMapper studentMapper;

    public StudentDtos.LinkedGuardianResponse share(Student student, StudentDtos.ShareStudentRequest request) {
        User invitedUser = userRepository.findByEmailIgnoreCase(request.email().trim().toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("The email does not belong to a registered user"));
        if (invitedUser.getId().equals(student.getUser().getId())) {
            throw new IllegalArgumentException("The owner already has access to this student");
        }

        StudentGuardian access = studentGuardianRepository
                .findByStudentIdAndUserIdAndDeletedAtIsNull(student.getId(), invitedUser.getId())
                .orElseGet(StudentGuardian::new);
        access.setStudent(student);
        access.setUser(invitedUser);
        access.setRelationshipRole(normalizeRelationshipRole(request.relationshipRole()));
        access.setStatus(STATUS_ACTIVE);
        access.setDeletedAt(null);
        return studentMapper.toGuardianResponse(studentGuardianRepository.save(access));
    }

    public List<StudentDtos.LinkedGuardianResponse> listGuardians(Student student) {
        List<StudentDtos.LinkedGuardianResponse> guardians = new ArrayList<>();
        guardians.add(new StudentDtos.LinkedGuardianResponse(
                null,
                student.getUser().getId(),
                student.getUser().getFullName(),
                student.getUser().getEmail(),
                student.getUser().getPhone(),
                RELATIONSHIP_OWNER,
                STATUS_ACTIVE,
                student.getCreatedAt()));
        guardians.addAll(studentGuardianRepository
                .findByStudentIdAndStatusAndDeletedAtIsNull(student.getId(), STATUS_ACTIVE)
                .stream()
                .map(studentMapper::toGuardianResponse)
                .toList());
        return guardians;
    }

    private String normalizeRelationshipRole(String relationshipRole) {
        if (relationshipRole == null || relationshipRole.isBlank()) {
            return RELATIONSHIP_VIEWER;
        }
        String normalized = relationshipRole.trim().toUpperCase();
        return switch (normalized) {
            case RELATIONSHIP_GUARDIAN, RELATIONSHIP_VIEWER -> normalized;
            default -> RELATIONSHIP_VIEWER;
        };
    }
}
