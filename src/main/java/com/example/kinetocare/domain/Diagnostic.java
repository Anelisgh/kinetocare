package com.example.kinetocare.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    @ManyToOne
    @JoinColumn(name = "pacient_id")
    private Pacient pacient;

    @ManyToOne
    @JoinColumn(name = "terapeut_id")
    private Terapeut terapeut;

    @OneToMany(mappedBy = "diagnostic")
    private List<Evaluare> evaluari = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "serviciu_id")
    private Serviciu serviciu;
}
