package com.example.terapeuti_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "concediu_terapeut",
        indexes = {
                @Index(name = "idx_concediu_terapeut", columnList = "terapeut_id"),
                @Index(name = "idx_concediu_date", columnList = "data_inceput, data_sfarsit")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConcediuTerapeut {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "terapeut_id", nullable = false)
    private Long terapeutId;

    @Column(name = "data_inceput", nullable = false)
    private LocalDate dataInceput;

    @Column(name = "data_sfarsit", nullable = false)
    private LocalDate dataSfarsit;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}