package com.example.user.domain;

import com.example.user.domain.security.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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
    private List<Pacient> pacienti;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;
}


