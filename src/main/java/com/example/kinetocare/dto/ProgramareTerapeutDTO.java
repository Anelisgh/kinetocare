package com.example.kinetocare.dto;

import com.example.kinetocare.domain.Serviciu;
import com.example.kinetocare.domain.Status;
import com.example.kinetocare.domain.TipServiciu;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProgramareTerapeutDTO {
    private Long programareId;
    private Long pacientId;
    private String numePacient;
    private LocalDate data;
    private LocalTime oraStart;
    private LocalTime oraEnd;
    private TipServiciu tipServiciu;
    private Status status;
}
