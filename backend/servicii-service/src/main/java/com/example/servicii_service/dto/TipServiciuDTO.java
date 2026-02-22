package com.example.servicii_service.dto;

import jakarta.validation.constraints.NotBlank;

public record TipServiciuDTO(
        Long id,
        @NotBlank(message = "Numele este obligatoriu")
        String nume,
        String descriere,
        Boolean active
) {
}
