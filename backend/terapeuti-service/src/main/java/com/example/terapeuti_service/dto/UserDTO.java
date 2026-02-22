package com.example.terapeuti_service.dto;

public record UserDTO(
        String keycloakId,
        String email,
        String nume,
        String prenume
) {
}
