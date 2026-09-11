package com.guardianescolar.api.modules.security.repository;

import com.guardianescolar.api.modules.security.domain.UserRole;
import com.guardianescolar.api.modules.security.domain.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {
}
