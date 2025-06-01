package com.example.programare.domain;

import com.example.common.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "programari")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Programare {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate data;
    private LocalTime ora;
    private LocalTime oraEnd;
    @Enumerated(EnumType.STRING)
    private Status status;
    @Column(name = "pacient_id")
    private Long pacientId;
    @Column(name = "terapeut_id")
    private Long terapeutId;
    @Column(name = "serviciu_id")
    private Long serviciuId;
}
