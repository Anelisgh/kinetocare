package com.example.user_service.dto;

import com.example.user_service.entity.Gen;
import com.example.user_service.entity.UserRole;

public record RegisterResponseDTO(
    String keycloakId,
    String email,
    String nume,
    String prenume,
    UserRole role,
    Gen gen,
    String message
) {
}