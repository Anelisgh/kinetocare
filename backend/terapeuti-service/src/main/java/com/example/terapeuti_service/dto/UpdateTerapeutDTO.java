package com.example.terapeuti_service.dto;

import com.example.terapeuti_service.entity.Specializare;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTerapeutDTO {
    @NotNull(message = "Specializarea este obligatorie")
    private Specializare specializare;
    private String pozaProfil;
}
