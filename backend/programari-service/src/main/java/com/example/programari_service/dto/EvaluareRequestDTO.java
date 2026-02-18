package com.example.programari_service.dto;

import com.example.programari_service.entity.TipEvaluare;
import lombok.Data;

@Data
public class EvaluareRequestDTO {
    private Long pacientId;
    private Long terapeutId;
    private Long programareId;
    private TipEvaluare tip; // INITIALA, REEVALUARE
    private String diagnostic;
    private Integer sedinteRecomandate;
    private Long serviciuRecomandatId;
    private String observatii;
}