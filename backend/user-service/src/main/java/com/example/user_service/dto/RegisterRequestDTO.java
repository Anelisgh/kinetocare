package com.example.user_service.dto;

import com.example.user_service.entity.Gen;
import com.example.user_service.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequestDTO(
    @NotBlank(message = "Email-ul este obligatoriu")
    @Email(message = "Email invalid")
    String email,

    @NotBlank(message = "Parola este obligatorie")
    @Size(min = 6, message = "Parola trebuie să aibă minim 6 caractere")
    String password,

    @NotBlank(message = "Numele este obligatoriu")
    @Size(min = 2, max = 100, message = "Numele trebuie să aibă între 2 și 100 caractere")
    String nume,

    @NotBlank(message = "Prenumele este obligatoriu")
    @Size(min = 2, max = 100, message = "Prenumele trebuie să aibă între 2 și 100 caractere")
    String prenume,

    @NotBlank(message = "Telefonul este obligatoriu")
    @Pattern(regexp = "^07[0-9]{8}$", message = "Număr de telefon invalid")
    String telefon,

    @NotNull(message = "Rolul este obligatoriu")
    UserRole role,

    @NotNull(message = "Genul este obligatoriu")
    Gen gen
) {
}