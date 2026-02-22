package com.example.servicii_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record ServiciuAdminDTO(
        Long id,
        @NotNull(message = "ID-ul tipului de serviciu este obligatoriu")
        Long tipServiciuId,
        String numeTip,
        String nume,
        @NotNull(message = "Prețul este obligatoriu")
        @Positive(message = "Prețul trebuie să fie pozitiv")
        BigDecimal pret,
        @NotNull(message = "Durata este obligatorie")
        @Positive(message = "Durata trebuie să fie pozitivă")
        Integer durataMinute,
        Boolean active
) {
}
