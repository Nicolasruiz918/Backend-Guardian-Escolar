package com.guardianescolar.api.modules.students.service;

import com.guardianescolar.api.modules.auth.service.CurrentUserService;
import com.guardianescolar.api.modules.students.domain.EmergencyContact;
import com.guardianescolar.api.modules.students.domain.Student;
import com.guardianescolar.api.modules.students.dto.EmergencyContactDtos;
import com.guardianescolar.api.modules.students.repository.EmergencyContactRepository;
import com.guardianescolar.api.modules.security.domain.User;
import com.guardianescolar.api.shared.exception.ResourceNotFoundException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmergencyContactService {

    private final EmergencyContactRepository emergencyContactRepository;
    private final StudentService studentService;
    private final CurrentUserService currentUserService;

    @Transactional(readOnly = true)
    public List<EmergencyContactDtos.EmergencyContactResponse> list(UUID studentId) {
        studentService.getAllowed(studentId);
        return emergencyContactRepository.findByStudentIdAndIsActiveTrueAndDeletedAtIsNull(studentId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public EmergencyContactDtos.EmergencyContactResponse create(
            UUID studentId,
            EmergencyContactDtos.EmergencyContactRequest request) {
        User current = currentUserService.currentUser();
        Student student = studentService.getManageable(studentId);
        EmergencyContact contact = new EmergencyContact();
        contact.setStudent(student);
        applyData(contact, request);
        contact.setIsActive(true);
        contact.setCreatedBy(current);
        return toResponse(emergencyContactRepository.save(contact));
    }

    @Transactional
    public EmergencyContactDtos.EmergencyContactResponse update(
            UUID studentId,
            UUID contactId,
            EmergencyContactDtos.EmergencyContactRequest request) {
        User current = currentUserService.currentUser();
        studentService.getManageable(studentId);
        EmergencyContact contact = getStudentContact(contactId, studentId);
        applyData(contact, request);
        contact.setUpdatedBy(current);
        return toResponse(emergencyContactRepository.save(contact));
    }

    @Transactional
    public void delete(UUID studentId, UUID contactId) {
        User current = currentUserService.currentUser();
        studentService.getManageable(studentId);
        EmergencyContact contact = getStudentContact(contactId, studentId);
        contact.setIsActive(false);
        contact.setDeletedAt(OffsetDateTime.now());
        contact.setUpdatedBy(current);
        emergencyContactRepository.save(contact);
    }

    private EmergencyContact getStudentContact(UUID contactId, UUID studentId) {
        EmergencyContact contact = emergencyContactRepository.findByIdAndDeletedAtIsNull(contactId)
                .orElseThrow(() -> new ResourceNotFoundException("Emergency contact not found"));
        if (!contact.getStudent().getId().equals(studentId)) {
            throw new ResourceNotFoundException("Emergency contact not found for this student");
        }
        return contact;
    }

    private void applyData(
            EmergencyContact contact,
            EmergencyContactDtos.EmergencyContactRequest request) {
        contact.setFullName(request.fullName());
        contact.setPhone(request.phone());
        contact.setRelationship(request.relationship());
        contact.setIsPrimary(Boolean.TRUE.equals(request.isPrimary()));
    }

    private EmergencyContactDtos.EmergencyContactResponse toResponse(EmergencyContact contact) {
        return new EmergencyContactDtos.EmergencyContactResponse(
                contact.getId(),
                contact.getStudent().getId(),
                contact.getFullName(),
                contact.getPhone(),
                contact.getRelationship(),
                contact.getIsPrimary(),
                contact.getIsActive());
    }
}
