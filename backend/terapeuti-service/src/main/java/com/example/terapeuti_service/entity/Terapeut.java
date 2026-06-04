package com.example.terapeuti_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "terapeuti",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_terapeuti_keycloak_id", columnNames = {"keycloak_id"})
        },
        indexes = {
                @Index(name = "idx_terapeuti_specializare_active", columnList = "specializare, active")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@lombok.experimental.SuperBuilder
public class Terapeut extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "keycloak_id", nullable = false, unique = true, length = 36)
    private String keycloakId;

    @Enumerated(EnumType.STRING)
    private Specializare specializare; // ADULTI / PEDIATRIE
// MEDIUMTEXT = 16MB
    @Column(name = "poza_profil", columnDefinition = "MEDIUMTEXT")
    private String pozaProfil; // Base64

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
}
