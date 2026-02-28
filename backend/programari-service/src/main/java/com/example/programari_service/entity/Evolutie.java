package com.example.programari_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.OffsetDateTime;

@Entity
@Table(name = "evolutii", indexes = {
                @Index(name = "idx_evolutie_pacient", columnList = "pacient_keycloak_id"),
                @Index(name = "idx_evolutie_terapeut", columnList = "terapeut_keycloak_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Evolutie {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "pacient_keycloak_id", nullable = false, length = 36)
        private String pacientKeycloakId;

        @Column(name = "terapeut_keycloak_id", nullable = false, length = 36)
        private String terapeutKeycloakId;

        @Column(columnDefinition = "TEXT", nullable = false)
        private String observatii;

        @CreationTimestamp
        @Column(name = "created_at", nullable = false, updatable = false)
        private OffsetDateTime createdAt;

        @UpdateTimestamp
        @Column(name = "updated_at")
        private OffsetDateTime updatedAt;
}
