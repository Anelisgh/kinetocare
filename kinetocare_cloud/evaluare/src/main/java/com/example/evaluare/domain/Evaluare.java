package com.example.evaluare.domain;

import com.example.common.enums.TipEvaluare;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "evaluari")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Evaluare {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "tip_evaluare")
    @Enumerated(EnumType.STRING)
    private TipEvaluare tipEvaluare;
    private LocalDate data;
    private String observatii;
    @Column(name = "terapeut_id")
    private Long terapeutId;
    @Column(name = "pacient_id")
    private Long pacientId;
    @Column(name = "diagnostic_id")
    private Long diagnosticId;
}

