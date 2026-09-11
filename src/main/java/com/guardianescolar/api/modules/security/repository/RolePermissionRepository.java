package com.guardianescolar.api.modules.security.repository;

import com.guardianescolar.api.modules.security.domain.RolePermission;
import com.guardianescolar.api.modules.security.domain.RolePermissionId;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RolePermissionRepository extends JpaRepository<RolePermission, RolePermissionId> {

    @Query("""
            select rp.permission.permissionName
            from RolePermission rp
            where rp.role.id in :roleIds
            """)
    List<String> findPermissionNamesByRoleIds(@Param("roleIds") Collection<UUID> roleIds);
}
