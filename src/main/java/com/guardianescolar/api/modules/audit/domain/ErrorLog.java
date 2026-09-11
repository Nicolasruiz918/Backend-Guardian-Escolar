package com.guardianescolar.api.modules.audit.domain;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.guardianescolar.api.modules.security.domain.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "error_logs")
@Getter
@Setter
public class ErrorLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "error_type", nullable = false, length = 100)
    private String tipoError;

    @Column(name = "description")
    private String description;

    @Column(name = "stack_trace")
    private String trazaError;

    @JdbcTypeCode(SqlTypes.INET)
    @Column(name = "source_ip", columnDefinition = "inet")
    private InetAddress ipOrigen;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadatos;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
