package com.guardianescolar.api.modules.trips.repository;

import com.guardianescolar.api.modules.trips.domain.StudentDevice;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentDeviceRepository extends JpaRepository<StudentDevice, UUID> {

    List<StudentDevice> findAllByStudentIdOrderByNameAsc(UUID studentId);

    Optional<StudentDevice> findByIdAndStudentId(UUID id, UUID studentId);

    Optional<StudentDevice> findByIdentifierAndStudentOwnerId(String identifier, UUID ownerId);
}
