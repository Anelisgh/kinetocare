package com.example.terapeuti_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalTime;
import java.time.OffsetDateTime;

@Entity
@Table(name = "disponibilitate_terapeut",
        indexes = {
                @Index(name = "idx_disp_terapeut", columnList = "terapeut_id"),
                @Index(name = "idx_disp_locatie", columnList = "locatie_id")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisponibilitateTerapeut {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "terapeut_id", nullable = false)
    private Long terapeutId;

    @Column(name = "zi_saptamana", nullable = false)
    private Integer ziSaptamana; // 1=Luni ... 7=Duminica

    @Column(name = "locatie_id", nullable = false)
    private Long locatieId;

    @Column(name = "ora_inceput", nullable = false)
    private LocalTime oraInceput;

    @Column(name = "ora_sfarsit", nullable = false)
    private LocalTime oraSfarsit;

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
