package com.example.kinetocare.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name="plati")
public class Plata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate data;
    private BigDecimal suma;
    // in sql -> stare_plata: 0 (achitata), 1(in_asteptare)
    @Column(name = "stare_plata")
    private StarePlata starePlata;

    @ManyToOne
    @JoinColumn(name = "programare_id")
    private Programare programare;

    @ManyToOne
    @JoinColumn(name = "pacient_id")
    private Pacient pacient;
}