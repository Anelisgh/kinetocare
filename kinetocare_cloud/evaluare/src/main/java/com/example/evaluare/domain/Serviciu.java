package com.example.evaluare.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.example.common.enums.TipServiciu;
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
