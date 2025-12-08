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
                @Index(name = "idx_eval_pacient", columnList = "pacient_id"),
                @Index(name = "idx_eval_terapeut", columnList = "terapeut_id"),
                @Index(name = "idx_eval_programare", columnList = "programare_id"),
                @Index(name = "idx_eval_tip", columnList = "tip")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Evaluare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pacient_id", nullable = false)
    private Long pacientId;

    @Column(name = "terapeut_id", nullable = false)
    private Long terapeutId;

    @Column(name = "programare_id")
    private Long programareId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipEvaluare tip;

    @Column(nullable = false)
    private LocalDate data;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String diagnostic;

    @Column(name = "sedinte_recomandate")
    private Integer sedinteRecomandate;

    @Column(name = "serviciu_recomandat_id")
    private Long serviciuRecomandatId;

    @Column(columnDefinition = "TEXT")
    private String observatii;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}