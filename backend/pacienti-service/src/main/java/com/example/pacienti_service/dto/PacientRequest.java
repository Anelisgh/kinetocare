package com.example.pacienti_service.dto;

import com.example.pacienti_service.entity.FaceSport;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PacientRequest{
    private LocalDate dataNasterii;
    private String cnp;
    private FaceSport faceSport;

    @Size(max = 500, message = "Detaliile despre sport trebuie să aibă maxim 500 de caractere")
    private String detaliiSport;

    private String orasPreferat;
    private Long locatiePreferataId;
    private String terapeutKeycloakId;
}
