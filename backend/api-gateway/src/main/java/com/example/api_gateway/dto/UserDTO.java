package com.example.api_gateway.dto;

public record UserDTO(
    Long id,
    String keycloakId,
    String email,
    String nume,
    String prenume,
    String role,
    Boolean active
) {}
