package com.guardianescolar.api.modules.students.service;

import com.guardianescolar.api.modules.auth.service.CurrentUserService;
import com.guardianescolar.api.modules.students.domain.Student;
import com.guardianescolar.api.modules.students.domain.StudentDevice;
import com.guardianescolar.api.modules.students.domain.EmergencyContact;
import com.guardianescolar.api.modules.students.dto.StudentDtos;
import com.guardianescolar.api.modules.students.repository.EmergencyContactRepository;
import com.guardianescolar.api.modules.students.repository.StudentDeviceRepository;
import com.guardianescolar.api.modules.students.domain.StudentGuardian;
import com.guardianescolar.api.modules.students.repository.StudentGuardianRepository;
import com.guardianescolar.api.modules.students.repository.StudentRepository;
import com.guardianescolar.api.modules.routes.domain.StudentRoute;
import com.guardianescolar.api.modules.routes.domain.Route;
import com.guardianescolar.api.modules.routes.repository.StudentRouteRepository;
import com.guardianescolar.api.modules.routes.repository.RouteRepository;
import com.guardianescolar.api.modules.security.domain.User;
import com.guardianescolar.api.modules.security.repository.UserRepository;
import com.guardianescolar.api.shared.exception.ResourceNotFoundException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentService {

    private static final int MIN_STUDENT_AGE = 3;
    private static final int MAX_STUDENT_AGE = 21;

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final RouteRepository routeRepository;
    private final StudentRouteRepository studentRouteRepository;
    private final StudentDeviceRepository studentDeviceRepository;
    private final StudentGuardianRepository studentGuardianRepository;
    private final EmergencyContactRepository emergencyContactRepository;
    private final CurrentUserService currentUserService;
    private final StudentMapper studentMapper;
    private final StudentAccessService studentAccessService;

    @Transactional(readOnly = true)
    public List<StudentDtos.StudentResponse> list() {
        User current = currentUserService.currentUser();
        List<Student> students = currentUserService.isAdmin(current)
                ? studentRepository.findByDeletedAtIsNullOrderByFullNameAsc()
                : studentRepository.findByUserEmailIgnoreCaseAndDeletedAtIsNullOrderByFullNameAsc(current.getEmail());
        return studentAccessService.visibleStudents(current, students)
                .stream()
                .map(studentMapper::toResponse)
                .toList();
    }

    @Transactional
    public StudentDtos.StudentResponse create(StudentDtos.StudentRequest request) {
        User current = currentUserService.currentUser();
        User owner = resolveOwner(request.userId(), current);

        Student student = new Student();
        student.setUser(owner);
        applyData(student, request);
        student.setIsActive(true);
        student.setCreatedBy(current);
        return studentMapper.toResponse(studentRepository.save(student));
    }

    @Transactional
    public StudentDtos.StudentResponse update(UUID studentId, StudentDtos.StudentRequest request) {
        User current = currentUserService.currentUser();
        Student student = getManageable(studentId);
        if (currentUserService.isAdmin(current) && request.userId() != null) {
            student.setUser(userRepository.findById(request.userId())
                    .orElseThrow(() -> new ResourceNotFoundException("Owner user not found")));
        }
        applyData(student, request);
        student.setUpdatedBy(current);
        return studentMapper.toResponse(studentRepository.save(student));
    }

    @Transactional
    public void delete(UUID studentId) {
        User current = currentUserService.currentUser();
        Student student = getManageable(studentId);
        student.setIsActive(false);
        student.setDeletedAt(OffsetDateTime.now());
        student.setUpdatedBy(current);
        studentRepository.save(student);
    }

    @Transactional(readOnly = true)
    public Student getAllowed(UUID studentId) {
        Student student = studentRepository.findByIdAndDeletedAtIsNull(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        studentAccessService.validateAccess(student);
        return student;
    }

    @Transactional(readOnly = true)
    public Student getManageable(UUID studentId) {
        Student student = studentRepository.findByIdAndDeletedAtIsNull(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        studentAccessService.validateManagement(student);
        return student;
    }

    @Transactional
    public void assignRoute(UUID studentId, UUID routeId) {
        Student student = getManageable(studentId);
        User current = currentUserService.currentUser();
        Route route = routeRepository.findByIdAndDeletedAtIsNull(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found"));
        studentAccessService.validateAssignableRoute(route, current);
        if (studentRouteRepository.existsByIdStudentIdAndIdRouteIdAndIsActiveTrue(studentId, routeId)) {
            throw new IllegalArgumentException("The student already has this route assigned");
        }
        studentRouteRepository.save(new StudentRoute(student, route));
    }

    @Transactional
    public StudentDtos.LinkedGuardianResponse share(UUID studentId, StudentDtos.ShareStudentRequest request) {
        Student student = getManageable(studentId);

        User invitedUser = userRepository.findByEmailIgnoreCase(request.email().trim().toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("The email does not belong to a registered user"));
        if (invitedUser.getId().equals(student.getUser().getId())) {
            throw new IllegalArgumentException("The owner already has access to this student");
        }

        StudentGuardian access = studentGuardianRepository
                .findByStudentIdAndUserIdAndDeletedAtIsNull(studentId, invitedUser.getId())
                .orElseGet(StudentGuardian::new);
        access.setStudent(student);
        access.setUser(invitedUser);
        access.setRelationshipRole(normalizeRelationshipRole(request.relationshipRole()));
        access.setStatus("ACTIVE");
        access.setDeletedAt(null);
        return studentMapper.toGuardianResponse(studentGuardianRepository.save(access));
    }

    @Transactional(readOnly = true)
    public List<StudentDtos.LinkedGuardianResponse> listGuardians(UUID studentId) {
        Student student = getAllowed(studentId);
        List<StudentDtos.LinkedGuardianResponse> guardians = new ArrayList<>();
        guardians.add(new StudentDtos.LinkedGuardianResponse(
                null,
                student.getUser().getId(),
                student.getUser().getFullName(),
                student.getUser().getEmail(),
                student.getUser().getPhone(),
                "OWNER",
                "ACTIVE",
                student.getCreatedAt()));
        guardians.addAll(studentGuardianRepository
                .findByStudentIdAndStatusAndDeletedAtIsNull(studentId, "ACTIVE")
                .stream()
                .map(studentMapper::toGuardianResponse)
                .toList());
        return guardians;
    }

    @Transactional
    public StudentDtos.StudentDeviceResponse linkDevice(
            StudentDtos.LinkDeviceRequest request) {
        Student student = studentRepository.findByIdAndDeletedAtIsNull(request.studentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        if (!linkCode(student.getId()).equalsIgnoreCase(request.code())) {
            throw new IllegalArgumentException("Invalid link code");
        }

        OffsetDateTime now = OffsetDateTime.now();
        String deviceIdentifier = request.deviceIdentifier().trim();
        StudentDevice activeDevice = studentDeviceRepository
                .findFirstByStudentIdAndIsActiveTrueAndDeletedAtIsNull(student.getId())
                .orElse(null);
        if (activeDevice != null
                && !activeDevice.getDeviceIdentifier().equals(deviceIdentifier)) {
            throw new IllegalArgumentException("This student already has a linked phone");
        }

        StudentDevice device = studentDeviceRepository
                .findByDeviceIdentifier(deviceIdentifier)
                .orElseGet(StudentDevice::new);
        if (device.getId() != null
                && !device.getStudent().getId().equals(student.getId())
                && Boolean.TRUE.equals(device.getIsActive())
                && device.getDeletedAt() == null) {
            throw new IllegalArgumentException("This phone is already linked to another student");
        }
        device.setStudent(student);
        device.setDeviceIdentifier(deviceIdentifier);
        device.setPlatform(request.platform());
        device.setDeviceName(normalizeOptionalText(request.deviceName()));
        device.setIsActive(true);
        if (device.getLinkedAt() == null) {
            device.setLinkedAt(now);
        }
        device.setLastUsedAt(now);
        device.setDeletedAt(null);
        device = studentDeviceRepository.save(device);

        return studentMapper.toDeviceResponse(device);
    }

    @Transactional(readOnly = true)
    public StudentDtos.LinkedProfileResponse linkedProfile(UUID studentId, String code) {
        Student student = studentRepository.findByIdAndDeletedAtIsNull(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        if (!linkCode(student.getId()).equalsIgnoreCase(code == null ? "" : code.trim())) {
            throw new IllegalArgumentException("Invalid link code");
        }

        List<EmergencyContact> contacts = emergencyContactRepository
                .findByStudentIdAndIsActiveTrueAndDeletedAtIsNull(studentId);
        EmergencyContact contact = contacts.stream()
                .filter(item -> Boolean.TRUE.equals(item.getIsPrimary()))
                .findFirst()
                .orElseGet(() -> contacts.stream().findFirst().orElse(null));

        return new StudentDtos.LinkedProfileResponse(
                student.getId(),
                linkCode(student.getId()),
                student.getFullName(),
                student.getSchoolGrade(),
                student.getBirthDate(),
                contact == null ? "" : contact.getFullName(),
                contact == null ? "" : contact.getPhone(),
                contact == null ? "" : contact.getRelationship());
    }

    private void applyData(Student student, StudentDtos.StudentRequest request) {
        validateBirthDate(request.birthDate());
        student.setFullName(request.fullName().trim());
        student.setSchoolGrade(request.schoolGrade());
        student.setBirthDate(request.birthDate());
    }

    private void validateBirthDate(LocalDate birthDate) {
        if (birthDate == null) {
            throw new IllegalArgumentException("Birth date is required");
        }
        LocalDate today = LocalDate.now();
        int age = Period.between(birthDate, today).getYears();
        if (age < MIN_STUDENT_AGE || age > MAX_STUDENT_AGE) {
            throw new IllegalArgumentException(
                    "The student's age must be between "
                            + MIN_STUDENT_AGE
                            + " and "
                            + MAX_STUDENT_AGE
                            + " years");
        }
    }

    private User resolveOwner(UUID userId, User current) {
        if (userId == null || !currentUserService.isAdmin(current)) {
            return current;
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner user not found"));
    }

    private String linkCode(UUID studentId) {
        return studentId.toString().replace("-", "").substring(0, 10).toUpperCase();
    }

    private String normalizeOptionalText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private String normalizeRelationshipRole(String relationshipRole) {
        if (relationshipRole == null || relationshipRole.isBlank()) {
            return "VIEWER";
        }
        String normalized = relationshipRole.trim().toUpperCase();
        return switch (normalized) {
            case "GUARDIAN", "VIEWER" -> normalized;
            default -> "VIEWER";
        };
    }

}
