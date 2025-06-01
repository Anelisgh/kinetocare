package com.example.user.domain;

import com.example.common.enums.Gen;
import com.example.common.enums.TipSport;
import com.example.user.domain.security.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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
    private Terapeut terapeut;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;
}

