package com.example.servicii_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiciuAdminDTO {
    private Long id;
    private Long tipServiciuId;
    private String numeTip; // Numele Tipului (ex: Kinetoterapie)
    private String nume; // Numele specific (ex: Sedinta scurta)
    private BigDecimal pret;
    private Integer durataMinute;
    private Boolean active;
}
