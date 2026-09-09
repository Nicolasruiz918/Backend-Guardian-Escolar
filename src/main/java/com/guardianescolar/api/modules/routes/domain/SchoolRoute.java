package com.guardianescolar.api.modules.routes.domain;

import com.guardianescolar.api.modules.students.domain.Student;
import com.guardianescolar.api.shared.persistence.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "routes")
public class SchoolRoute extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(name = "origin_name", length = 160)
    private String originName;

    @Column(name = "destination_name", length = 160)
    private String destinationName;

    @Column(nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sequenceNumber ASC")
    private List<RoutePoint> points = new ArrayList<>();

    protected SchoolRoute() {
    }

    public SchoolRoute(Student student, String name) {
        this.student = student;
        this.name = name;
    }

    public Student getStudent() { return student; }
    public String getName() { return name; }
    public String getOriginName() { return originName; }
    public String getDestinationName() { return destinationName; }
    public boolean isActive() { return active; }
    public List<RoutePoint> getPoints() { return points; }

    public void update(String name, String originName, String destinationName, boolean active) {
        this.name = name;
        this.originName = originName;
        this.destinationName = destinationName;
        this.active = active;
    }

    public void replacePoints(List<RoutePoint> newPoints) {
        points.clear();
        points.addAll(newPoints);
    }
}
