package com.example.programari_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SituatiePacientDTO {
    private String diagnostic;
    private Integer sedinteRecomandate;
    private Long sedinteEfectuate;
    private Long sedinteRamase;
}
