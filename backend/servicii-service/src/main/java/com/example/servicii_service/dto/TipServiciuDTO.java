package com.example.servicii_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TipServiciuDTO {
    private Long id;
    private String nume;
    private String descriere;
    private Boolean active;
}
