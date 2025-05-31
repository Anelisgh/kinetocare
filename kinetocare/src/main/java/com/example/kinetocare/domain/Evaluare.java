package com.example.kinetocare.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="evaluari")
public class Evaluare {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "tip_evaluare")
    @Enumerated(EnumType.STRING)
    private TipEvaluare tipEvaluare;
    private LocalDate data;
    private String observatii;

    @ManyToOne
    @JoinColumn(name = "diagnostic_id")
    private Diagnostic diagnostic;

    @ManyToOne
    @JoinColumn(name = "pacient_id")
    private Pacient pacient;

    @ManyToOne
    @JoinColumn(name = "terapeut_id")
    private Terapeut terapeut;
}
