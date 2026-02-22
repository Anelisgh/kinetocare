package com.example.pacienti_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "jurnal_pacient",
        indexes = {
                @Index(name = "idx_jurnal_pacient", columnList = "pacient_id"),
                @Index(name = "idx_jurnal_programare", columnList = "programare_id"),
                @Index(name = "idx_jurnal_data", columnList = "data")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JurnalPacient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pacient_id", nullable = false)
    private Long pacientId;

    @Column(name = "programare_id", nullable = false)
    private Long programareId;

    @Column(nullable = false)
    private LocalDate data;

    @Column(name = "nivel_durere", nullable = false)
    private Integer nivelDurere; // 1-10

    @Column(name = "dificultate_exercitii", nullable = false)
    private Integer dificultateExercitii; // 1-10

    @Column(name = "nivel_oboseala", nullable = false)
    private Integer nivelOboseala; // 1-10

    @Column(columnDefinition = "TEXT")
    private String comentarii;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}
