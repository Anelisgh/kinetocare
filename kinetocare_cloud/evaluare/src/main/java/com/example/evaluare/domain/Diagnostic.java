package com.example.evaluare.domain;


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
@Table(name="diagnostice")
public class Diagnostic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nume;
    private LocalDate data;
    @Column(name = "sedinte_recomandate", nullable = false)
    private Integer sedinteRecomandate; // adica nr de sedinte pana la reevaluare
    @Column(name = "pacient_id")
    private Long pacientId;
    @Column(name = "terapeut_id")
    private Long terapeutId;
    @Column(name = "serviciu_id")
    private Long serviciuId;
}
