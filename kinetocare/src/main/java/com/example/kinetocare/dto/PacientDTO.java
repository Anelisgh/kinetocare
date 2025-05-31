package com.example.kinetocare.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PacientDTO {
    private Long id;
    private String nume;
    private String varsta;
    private String diagnostic;
}
