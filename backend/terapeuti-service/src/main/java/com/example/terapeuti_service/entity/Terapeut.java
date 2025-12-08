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
                @Index(name = "idx_terapeuti_specializare", columnList = "specializare")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Terapeut {

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

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
