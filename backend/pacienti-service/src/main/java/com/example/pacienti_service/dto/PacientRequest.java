package com.example.pacienti_service.dto;

import com.example.pacienti_service.entity.FaceSport;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PacientRequest(
        LocalDate dataNasterii,
        String cnp,
        FaceSport faceSport,

        @Size(max = 500, message = "Detaliile despre sport trebuie să aibă maxim 500 de caractere")
        String detaliiSport,

        String orasPreferat,
        Long locatiePreferataId,
        String terapeutKeycloakId
) {}
