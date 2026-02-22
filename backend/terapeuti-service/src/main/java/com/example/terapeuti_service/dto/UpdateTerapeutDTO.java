package com.example.terapeuti_service.dto;

import com.example.terapeuti_service.entity.Specializare;
import jakarta.validation.constraints.NotNull;

public record UpdateTerapeutDTO(
        @NotNull(message = "Specializarea este obligatorie")
        Specializare specializare,
        String pozaProfil
) {}
