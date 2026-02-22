package com.example.user_service.dto;

import java.time.OffsetDateTime;

public record AdminUserDTO(
    Long id,
    String nume,
    String prenume,
    String email,
    String telefon,
    String role,
    Boolean active,
    OffsetDateTime createdAt
) {
}
