package com.example.kinetocare.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="programari")
public class Programare {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate data;
    private LocalTime ora;
    @Column(name = "ora_end")
    private LocalTime oraEnd;

    @Enumerated(EnumType.STRING)
    private Status status;

    @PrePersist
    @PreUpdate
    private void calculateOraEnd() {
        if (this.serviciu != null && this.ora != null) {
            this.oraEnd = this.ora.plusMinutes(this.serviciu.getDurataMinute());
        }
    }

    @ManyToOne
    @JoinColumn(name = "pacient_id")
    private Pacient pacient;

    @ManyToOne
    @JoinColumn(name = "terapeut_id")
    private Terapeut terapeut;

    @OneToMany(mappedBy = "programare")
    private List<Plata> plati = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "serviciu_id")
    private Serviciu serviciu;
}
