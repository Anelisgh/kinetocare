package com.example.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PacientHomeDTO {
    // Date despre pacient/header
    private String nume;
    private String diagnostic;
    private Integer varsta;
    private Integer sedintePanaLaReevaluare;

    // Programare
    private ProgramareDTO urmatoareaProgramare;
    private ProgramareDTO nouaProgramare;

    // Footer
    private String numarTerapeut; // numarul de telefon
}
