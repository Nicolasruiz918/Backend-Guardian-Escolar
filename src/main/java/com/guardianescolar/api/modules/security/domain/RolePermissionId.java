package com.guardianescolar.api.modules.security.domain;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
@Getter
@Setter
public class RolePermissionId implements Serializable {

    @Column(name = "role_id", columnDefinition = "uuid")
    private UUID roleId;

    @Column(name = "permission_id", columnDefinition = "uuid")
    private UUID permissionId;

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof RolePermissionId other)) {
            return false;
        }
        return Objects.equals(roleId, other.roleId) && Objects.equals(permissionId, other.permissionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleId, permissionId);
    }
}
