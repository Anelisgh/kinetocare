package com.example.pacienti_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "pacienti",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_pacienti_keycloak_id", columnNames = {"keycloak_id"}),
                @UniqueConstraint(name = "uk_pacienti_cnp", columnNames = {"cnp"})
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pacient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "keycloak_id", nullable = false, length = 36)
    private String keycloakId; // legătura cu User

    @Column(name = "data_nasterii")
    private LocalDate dataNasterii;

    @Column(length = 13)
    private String cnp;

    @Enumerated(EnumType.STRING)
    @Column(length = 5)
    private FaceSport faceSport;

    @Column(name = "detalii_sport", length = 500)
    private String detaliiSport;

    @Column(name = "oras_preferat", length = 100)
    private String orasPreferat;

    @Column(name = "locatie_preferata_id")
    private Long locatiePreferataId;

    @Column(name = "terapeut_keycloak_id", length = 36)
    private String terapeutKeycloakId; // doar preferinta, relatia se cimenteaza in serviciu programari (RelatiePacientTerapeut) dupa prima evaluare

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}