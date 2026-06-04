package com.example.programari_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "evaluari",
        indexes = {
                @Index(name = "idx_eval_pacient_data", columnList = "pacient_keycloak_id, data"),
                @Index(name = "idx_eval_terapeut", columnList = "terapeut_keycloak_id"),
                @Index(name = "idx_eval_programare", columnList = "programare_id"),
                @Index(name = "idx_eval_tip", columnList = "tip")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@lombok.experimental.SuperBuilder
public class Evaluare extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pacient_keycloak_id", nullable = false, length = 36)
    private String pacientKeycloakId;

    @Column(name = "terapeut_keycloak_id", nullable = false, length = 36)
    private String terapeutKeycloakId;

    @Column(name = "programare_id")
    private Long programareId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipEvaluare tip;

    @Column(nullable = false)
    private LocalDate data; // data programarii

    @Column(columnDefinition = "TEXT", nullable = false)
    private String diagnostic;

    @Column(name = "sedinte_recomandate")
    private Integer sedinteRecomandate;

    @Column(name = "serviciu_recomandat_id")
    private Long serviciuRecomandatId;

    @Column(columnDefinition = "TEXT")
    private String observatii;
}