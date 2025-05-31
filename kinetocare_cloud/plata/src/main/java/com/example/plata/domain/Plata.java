package com.example.plata.domain;

import com.example.common.enums.StarePlata;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "plati")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Plata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate data;
    private BigDecimal suma;
    @Enumerated(EnumType.STRING)
    private StarePlata starePlata;

    @Column(name = "programare_id")
    private Long programareId;
    @Column(name = "pacient_id")
    private Long pacientId;
}
