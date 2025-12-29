package com.example.programari_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "relatie_pacient_terapeut",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_pacient_activ",
                        columnNames = {"pacient_id", "activa"}
                )
        },
        indexes = {
                @Index(name = "idx_rel_pacient", columnList = "pacient_id"),
                @Index(name = "idx_rel_terapeut", columnList = "terapeut_id"),
                @Index(name = "idx_rel_activa", columnList = "activa")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RelatiePacientTerapeut {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pacient_id", nullable = false)
    private Long pacientId;

    @Column(name = "terapeut_id", nullable = false)
    private Long terapeutId;

    @Column(name = "data_inceput", nullable = false)
    private LocalDate dataInceput;

    @Column(name = "data_sfarsit")
    private LocalDate dataSfarsit;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activa = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}