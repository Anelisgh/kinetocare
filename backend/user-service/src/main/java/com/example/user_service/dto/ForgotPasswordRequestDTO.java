package com.example.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequestDTO(
    @NotBlank(message = "Email-ul nu poate fi gol")
    @Email(message = "Email invalid")
    String email
) {
}
