package com.example.kinetocare.domain;


import com.example.kinetocare.domain.security.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "pacienti")
public class Pacient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nume;
    private String telefon;
    private String email;
    @Enumerated(EnumType.STRING)
    private Gen gen;
    @Column(unique = true)
    private String cnp;
    @Column(name = "data_nastere")
    private LocalDate dataNastere;
    @Enumerated(EnumType.STRING)
    @Column(name = "tip_sport")
    private TipSport tipSport;
    @Column(name = "detalii_sport", nullable = true)
    private String detaliiSport; // in cazul in care face sport de performanta, se va preciza tipul

    @ManyToOne
    @JoinColumn(name = "terapeut_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Terapeut terapeut;

    @OneToMany(mappedBy = "pacient")
    private List<Programare> programari = new ArrayList<>();

    @OneToMany(mappedBy = "pacient")
    private List<Diagnostic> diagnostice = new ArrayList<>();

    @OneToMany(mappedBy = "pacient")
    private List<Plata> plati = new ArrayList<>();

    @OneToMany(mappedBy = "pacient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Evolutie> evolutii = new ArrayList<>();

    @OneToMany(mappedBy = "pacient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Evaluare> evaluari = new ArrayList<>();

    @OneToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    public Pacient(User user) {
        this.user = user;
    }

}
