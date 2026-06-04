package com.example.programari_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.OffsetDateTime;

@Entity
@Table(name = "evolutii", indexes = {
                @Index(name = "idx_evolutie_pacient_terapeut", columnList = "pacient_keycloak_id, terapeut_keycloak_id, created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@lombok.experimental.SuperBuilder
public class Evolutie extends BaseAuditableEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "pacient_keycloak_id", nullable = false, length = 36)
        private String pacientKeycloakId;

        @Column(name = "terapeut_keycloak_id", nullable = false, length = 36)
        private String terapeutKeycloakId;

        @Column(columnDefinition = "TEXT", nullable = false)
        private String observatii;
}
