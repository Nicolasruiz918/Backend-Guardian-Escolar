package com.guardianescolar.api.modules.students.service;

import com.guardianescolar.api.modules.auth.service.CurrentUserService;
import com.guardianescolar.api.modules.routes.domain.Route;
import com.guardianescolar.api.modules.security.domain.User;
import com.guardianescolar.api.modules.students.domain.Student;
import com.guardianescolar.api.modules.students.domain.StudentGuardian;
import com.guardianescolar.api.modules.students.repository.StudentGuardianRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentAccessService {

    private final CurrentUserService currentUserService;
    private final StudentGuardianRepository studentGuardianRepository;

    public List<Student> visibleStudents(User current, List<Student> ownedStudents) {
        if (currentUserService.isAdmin(current)) {
            return ownedStudents;
        }

        Map<UUID, Student> visibleStudents = new LinkedHashMap<>();
        ownedStudents.forEach(student -> visibleStudents.put(student.getId(), student));
        studentGuardianRepository.findByUserIdAndStatusAndDeletedAtIsNull(current.getId(), "ACTIVE")
                .stream()
                .map(StudentGuardian::getStudent)
                .filter(student -> student.getDeletedAt() == null)
                .forEach(student -> visibleStudents.put(student.getId(), student));
        return new ArrayList<>(visibleStudents.values());
    }

    public void validateAccess(Student student) {
        User current = currentUserService.currentUser();
        boolean owner = student.getUser().getId().equals(current.getId());
        boolean shared = studentGuardianRepository.existsByStudentIdAndUserIdAndStatusAndDeletedAtIsNull(
                student.getId(),
                current.getId(),
                "ACTIVE");
        if (!currentUserService.isAdmin(current) && !owner && !shared) {
            throw new AccessDeniedException("You do not have access to this student");
        }
    }

    public void validateManagement(Student student) {
        User current = currentUserService.currentUser();
        boolean owner = student.getUser().getId().equals(current.getId());
        if (!currentUserService.isAdmin(current) && !owner) {
            throw new AccessDeniedException("Only the owner can modify this student");
        }
    }

    public void validateAssignableRoute(Route route, User current) {
        if (currentUserService.isAdmin(current) || route.getCreatedBy() == null) {
            return;
        }
        if (!route.getCreatedBy().getId().equals(current.getId())) {
            throw new AccessDeniedException("You do not have access to this route");
        }
    }
}
