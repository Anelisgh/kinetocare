package com.example.pacienti_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PacientKeycloakDTO {
    private Long id;
    private String keycloakId;
}