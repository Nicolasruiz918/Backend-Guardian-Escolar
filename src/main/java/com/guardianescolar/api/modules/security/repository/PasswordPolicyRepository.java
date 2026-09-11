package com.guardianescolar.api.modules.security.repository;

import com.guardianescolar.api.modules.security.domain.PasswordPolicy;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordPolicyRepository extends JpaRepository<PasswordPolicy, UUID> {
}
