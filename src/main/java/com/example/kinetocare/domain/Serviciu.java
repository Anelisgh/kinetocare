package com.example.kinetocare.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="servicii")
public class Serviciu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(name = "tip_serviciu")
    private TipServiciu tipServiciu;
    private String descriere;
    private BigDecimal pret;
    private Integer durataMinute;
}

//SQL
//INSERT INTO servicii (tip_serviciu, descriere, pret, durata_minute) VALUES
//('EVALUARE', 'Examinare inițială a pacientului pentru identificarea nevoilor terapeutice și stabilirea unui plan de tratament personalizat.', 250.00, 30),
//('REEVALUARE', 'Analiză periodică a progresului pacientului și ajustarea programului de tratament în funcție de evoluția sa.', 200.00, 30),
//('OSTEOPATIE', 'Ședință de osteopatie destinată echilibrării structurale și funcționale a organismului prin tehnici manuale.', 200.00, 30),
//('TERAPIE_MOTORIE', 'Sesiune de terapie motrică pentru îmbunătățirea mobilității, coordonării și forței musculare.', 200.00, 30),
//('LOGOPEDIE', 'Consultație logopedică axată pe evaluarea și îmbunătățirea abilităților de comunicare și pronunție.', 140.00, 30),
//('MASAJ', 'Masaj terapeutic destinat relaxării musculare, ameliorării durerilor și îmbunătățirii circulației sanguine.', 100.00, 30);

