package com.example.programari_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;

@Entity
@Table(name = "programari",
        indexes = {
                @Index(name = "idx_prog_terapeut_data_status", columnList = "terapeut_id, data, status"),
                @Index(name = "idx_prog_pacient_data", columnList = "pacient_id, data"),
                @Index(name = "idx_prog_status_data", columnList = "status, data")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Programare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pacient_id", nullable = false)
    private Long pacientId;

    @Column(name = "terapeut_id", nullable = false)
    private Long terapeutId;

    @Column(name = "locatie_id", nullable = false)
    private Long locatieId;

    @Column(name = "serviciu_id", nullable = false)
    private Long serviciuId;

    // tipServiciu, pret, durataMinute le copiam din serviciu la creare
    @Column(name = "tip_serviciu", nullable = false, length = 100)
    private String tipServiciu;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal pret;

    @Column(name = "durata_minute", nullable = false)
    private Integer durataMinute;

    @Column(name = "prima_intalnire")
    private Boolean primaIntalnire; // TRUE = evaluare inițiala

    @Column(nullable = false)
    private LocalDate data;

    @Column(name = "ora_inceput", nullable = false)
    private LocalTime oraInceput;

    @Column(name = "ora_sfarsit", nullable = false)
    private LocalTime oraSfarsit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusProgramare status;

    @Enumerated(EnumType.STRING)
    @Column(name = "motiv_anulare", length = 30)
    private MotivAnulare motivAnulare;

    @Column(name = "are_evaluare", nullable = false)
    @Builder.Default
    private Boolean areEvaluare = false;

    @Column(name = "are_jurnal", nullable = false)
    @Builder.Default
    private Boolean areJurnal = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
