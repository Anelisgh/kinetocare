package com.example.pacienti_service.dto;

import com.example.pacienti_service.entity.FaceSport;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PacientCompleteProfileRequest {
    @NotNull(message = "Data nașterii este obligatorie.")
    LocalDate dataNasterii;

    @NotBlank(message = "CNP-ul este obligatoriu.")
    String cnp;

    @NotNull(message = "Statusul 'Face sport' este obligatoriu.")
    FaceSport faceSport;

    String detaliiSport;
}
