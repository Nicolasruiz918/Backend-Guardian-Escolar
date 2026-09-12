package com.guardianescolar.api.modules.students.repository;

import com.guardianescolar.api.modules.students.domain.EmergencyContact;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmergencyContactRepository extends JpaRepository<EmergencyContact, UUID> {

    List<EmergencyContact> findByStudentIdAndIsActiveTrueAndDeletedAtIsNull(UUID studentId);

    Optional<EmergencyContact> findByIdAndDeletedAtIsNull(UUID id);
}
