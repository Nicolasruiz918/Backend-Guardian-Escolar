package com.guardianescolar.api.modules.auth.repository;

import com.guardianescolar.api.modules.auth.domain.VerificationCode;
import com.guardianescolar.api.modules.auth.domain.VerificationCodeType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, UUID> {

    Optional<VerificationCode> findByIdAndType(UUID id, VerificationCodeType type);

    Optional<VerificationCode> findByResetTokenHash(String resetTokenHash);
}
