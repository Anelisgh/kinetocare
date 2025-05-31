package com.example.common.dto;

import com.example.common.enums.Status;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProgramareDTO {
    private Long id;
    @NotNull(message = "Data este obligatorie")
    @FutureOrPresent(message = "Data nu poate fi în trecut")
    private LocalDate data;
    @NotNull(message = "Ora este obligatorie")
    private LocalTime ora;
    private Status status;
    private Long pacientId;
    private Long serviciuId;
}
