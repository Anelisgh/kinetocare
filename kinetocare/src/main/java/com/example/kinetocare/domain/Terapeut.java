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
@Table(name = "terapeuti")
public class Terapeut {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nume;
    private String telefon;
    @Column(name = "data_nastere")
    private LocalDate dataNastere;
    @Column(unique = true)
    private String cnp;

    @OneToMany(mappedBy = "terapeut")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Pacient> pacienti = new ArrayList<>();

    @OneToMany(mappedBy = "terapeut")
    private List<Programare> programari = new ArrayList<>();

    @OneToMany(mappedBy = "terapeut")
    private List<Evaluare> evaluari = new ArrayList<>();

    @OneToMany(mappedBy = "terapeut")
    private List<Evolutie> evolutii = new ArrayList<>();

    @OneToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    public Terapeut(User user) {
        this.user = user;
    }
}

