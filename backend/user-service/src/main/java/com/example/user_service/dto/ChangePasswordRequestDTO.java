package com.example.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequestDTO(
    @NotBlank(message = "Parola nu poate fi goală")
    @Size(min = 6, message = "Parola trebuie să aibă minim 6 caractere")
    String newPassword
) {
}
