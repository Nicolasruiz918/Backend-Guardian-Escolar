package com.guardianescolar.api.modules.routes.domain;

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
public class StudentRouteId implements Serializable {

    @Column(name = "student_id", columnDefinition = "uuid")
    private UUID studentId;

    @Column(name = "route_id", columnDefinition = "uuid")
    private UUID routeId;

    public StudentRouteId() {
    }

    public StudentRouteId(UUID studentId, UUID routeId) {
        this.studentId = studentId;
        this.routeId = routeId;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof StudentRouteId other)) {
            return false;
        }
        return Objects.equals(studentId, other.studentId) && Objects.equals(routeId, other.routeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, routeId);
    }
}
