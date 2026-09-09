package com.guardianescolar.api.modules.students.service;

import com.guardianescolar.api.modules.auth.domain.UserAccount;
import com.guardianescolar.api.modules.auth.repository.UserAccountRepository;
import com.guardianescolar.api.modules.students.domain.Student;
import com.guardianescolar.api.modules.students.dto.StudentRequest;
import com.guardianescolar.api.modules.students.dto.StudentResponse;
import com.guardianescolar.api.modules.students.repository.StudentRepository;
import com.guardianescolar.api.shared.exception.ApiException;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StudentService {

    private final StudentRepository students;
    private final UserAccountRepository users;

    public StudentService(StudentRepository students, UserAccountRepository users) {
        this.students = students;
        this.users = users;
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> list(UUID ownerId) {
        return students.findAllByOwnerIdOrderByFullNameAsc(ownerId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public StudentResponse get(UUID ownerId, UUID studentId) {
        return toResponse(findOwned(ownerId, studentId));
    }

    public StudentResponse create(UUID ownerId, StudentRequest request) {
        UserAccount owner = users.findById(ownerId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
        Student student = new Student(owner, normalizeRequired(request.fullName()));
        apply(student, request);
        return toResponse(students.save(student));
    }

    public StudentResponse update(UUID ownerId, UUID studentId, StudentRequest request) {
        Student student = findOwned(ownerId, studentId);
        apply(student, request);
        return toResponse(student);
    }

    public void delete(UUID ownerId, UUID studentId) {
        students.delete(findOwned(ownerId, studentId));
    }

    public Student findOwned(UUID ownerId, UUID studentId) {
        return students.findByIdAndOwnerId(studentId, ownerId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Student not found"));
    }

    private void apply(Student student, StudentRequest request) {
        student.update(normalizeRequired(request.fullName()), normalize(request.grade()), request.age(), normalize(request.school()),
                normalize(request.avatarUrl()), normalize(request.emergencyContactName()),
                normalize(request.emergencyContactPhone()), request.active() == null || request.active());
    }

    private StudentResponse toResponse(Student student) {
        return new StudentResponse(student.getId(), student.getFullName(), student.getGrade(), student.getAge(),
                student.getSchool(), student.getAvatarUrl(), student.getEmergencyContactName(),
                student.getEmergencyContactPhone(), student.isActive());
    }

    private String normalizeRequired(String value) {
        return value.trim();
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
