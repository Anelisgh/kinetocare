package com.example.user_service.dto;

import com.example.user_service.entity.Gen;

public record UserDTO(
    Long id,
    String keycloakId,
    String nume,
    String prenume,
    String email,
    String telefon,
    Gen gen
) {
}
