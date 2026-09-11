package com.guardianescolar.api.modules.security.repository;

import com.guardianescolar.api.modules.security.domain.Role;
import com.guardianescolar.api.modules.security.domain.RoleName;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, UUID> {

    Optional<Role> findByRoleName(RoleName roleName);
}
