package com.guardianescolar.api.modules.auth.service;

import com.guardianescolar.api.modules.security.domain.Permission;
import com.guardianescolar.api.modules.security.domain.Role;
import com.guardianescolar.api.modules.security.domain.RoleName;
import com.guardianescolar.api.modules.security.domain.RolePermission;
import com.guardianescolar.api.modules.security.domain.RolePermissionId;
import com.guardianescolar.api.modules.security.repository.PermissionRepository;
import com.guardianescolar.api.modules.security.repository.RolePermissionRepository;
import com.guardianescolar.api.modules.security.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleProvisioningService {

    private static final String[] PARENT_PERMISSIONS = {
            "STUDENT_READ",
            "STUDENT_CREATE",
            "STUDENT_UPDATE",
            "STUDENT_DELETE",
            "SAFE_ZONE_READ",
            "SAFE_ZONE_CREATE",
            "SAFE_ZONE_UPDATE",
            "SAFE_ZONE_DELETE",
            "ROUTE_READ",
            "ROUTE_CREATE",
            "ROUTE_UPDATE",
            "ROUTE_DELETE",
            "LOCATION_VIEW"
    };

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    public Role parentRole() {
        Role parentRole = roleRepository.findByRoleName(RoleName.PARENT)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setRoleName(RoleName.PARENT);
                    role.setDescription("Padre de familia - acceso a ubicación de hijos");
                    return roleRepository.save(role);
                });
        ensureParentPermissions(parentRole);
        return parentRole;
    }

    private void ensureParentPermissions(Role parentRole) {
        for (String permissionName : PARENT_PERMISSIONS) {
            Permission permission = permissionRepository.findByPermissionName(permissionName)
                    .orElseGet(() -> {
                        Permission newPermission = new Permission();
                        newPermission.setPermissionName(permissionName);
                        newPermission.setDescription("Permission " + permissionName);
                        return permissionRepository.save(newPermission);
                    });

            RolePermissionId id = new RolePermissionId();
            id.setRoleId(parentRole.getId());
            id.setPermissionId(permission.getId());
            if (!rolePermissionRepository.existsById(id)) {
                RolePermission rolePermission = new RolePermission();
                rolePermission.setId(id);
                rolePermission.setRole(parentRole);
                rolePermission.setPermission(permission);
                rolePermissionRepository.save(rolePermission);
            }
        }
    }
}
