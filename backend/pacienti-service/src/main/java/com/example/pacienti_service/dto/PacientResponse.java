package com.example.pacienti_service.dto;

import com.example.pacienti_service.entity.FaceSport;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PacientResponse {
    private Long id;
    private String keycloakId;
    private LocalDate dataNasterii;
    private String cnp;
    private FaceSport faceSport;
    private String detaliiSport;
    private String orasPreferat;
    private Long locatiePreferataId;
    private String terapeutKeycloakId;
}