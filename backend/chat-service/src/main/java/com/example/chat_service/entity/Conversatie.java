package com.example.chat_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "conversatii",
        indexes = {
                @Index(name = "idx_conv_pacient_kc", columnList = "pacient_keycloak_id"),
                @Index(name = "idx_conv_terapeut_kc", columnList = "terapeut_keycloak_id"),
                @Index(name = "idx_conv_ultim_mesaj", columnList = "ultimul_mesaj_la")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conversatie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pacient_keycloak_id", nullable = false, length = 36)
    private String pacientKeycloakId;

    @Column(name = "terapeut_keycloak_id", nullable = false, length = 36)
    private String terapeutKeycloakId;

    @Column(name = "ultimul_mesaj_la")
    private OffsetDateTime ultimulMesajLa;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
