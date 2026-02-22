package com.example.pacienti_service.dto;

import com.example.pacienti_service.entity.FaceSport;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record PacientCompleteProfileRequest(
        @NotNull(message = "Data nașterii este obligatorie.")
        LocalDate dataNasterii,

        @NotBlank(message = "CNP-ul este obligatoriu.")
        String cnp,

        @NotNull(message = "Statusul 'Face sport' este obligatoriu.")
        FaceSport faceSport,

        String detaliiSport
) {}
