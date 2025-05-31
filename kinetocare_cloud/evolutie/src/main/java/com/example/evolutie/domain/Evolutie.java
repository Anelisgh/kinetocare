package com.example.evolutie.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "evolutie")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Evolutie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String observatii;
    private LocalDate data;

    @Column(name = "pacient_id")
    private Long pacientId;

    @Column(name = "terapeut_id")
    private Long terapeutId;
}
