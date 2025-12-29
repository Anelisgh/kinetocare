package com.example.programari_service.dto;

import lombok.Data;

// pentru a lua doar id-ul de legatura din pacienti-service
@Data
public class PacientKeycloakDTO {
    private Long id;
    private String keycloakId;
}