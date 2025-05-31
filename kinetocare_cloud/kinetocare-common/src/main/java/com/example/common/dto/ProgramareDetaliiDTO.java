package com.example.common.dto;

import com.example.common.enums.Status;
import com.example.common.enums.TipServiciu;
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
public class ProgramareDetaliiDTO {
    private Long id;
    private LocalDate data;
    private LocalTime ora;
    private LocalTime oraEnd;
    private Status status;
    private String numePacient;
    private String numeTerapeut;
    private TipServiciu tipServiciu;
    private Long pacientId;
    private Long serviciuId;
}
