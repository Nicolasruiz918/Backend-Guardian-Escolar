package com.guardianescolar.api.modules.security.domain;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "password_policies")
@Getter
@Setter
public class PasswordPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "min_length")
    private Integer longitudeMinima = 10;

    @Column(name = "max_length")
    private Integer longitudeMaxima = 128;

    @Column(name = "requires_uppercase")
    private Boolean requiereMayusculas = true;

    @Column(name = "requires_numbers")
    private Boolean requiereNumeros = true;

    @Column(name = "requires_symbols")
    private Boolean requiereSimbolos = true;

    @Column(name = "expiration_days")
    private Integer diasExpiracion = 90;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;

}
