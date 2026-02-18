package com.example.programari_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO pentru lista de pacienti a terapeutului (card view)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FisaPacientDTO {
    private Long pacientId;
    private String nume;
    private String prenume;
    private Integer varsta;
    private String diagnostic; // ultimul diagnostic
    private Long sedinteRamase;
    private Boolean activ; // relatia activa sau nu
}
