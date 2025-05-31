package com.example.common.dto;

import com.example.common.enums.TipServiciu;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiciuDTO {
    private Long id;
    private TipServiciu tipServiciu;
    private String descriere;
    private BigDecimal pret;
    private Integer durataMinute;
}
