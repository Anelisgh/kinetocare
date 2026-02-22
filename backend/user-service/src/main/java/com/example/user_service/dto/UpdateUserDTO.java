package com.example.user_service.dto;

import com.example.user_service.entity.Gen;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

public record UpdateUserDTO(
    @Size(min = 2, max = 100, message = "Numele trebuie să aibă între 2 și 100 caractere")
    String nume,
    
    @Size(min = 2, max = 100, message = "Prenumele trebuie să aibă între 2 și 100 caractere")
    String prenume,
    
    @Email(message = "Email invalid")
    String email,
    
    @Pattern(regexp = "^07[0-9]{8}$", message = "Număr de telefon invalid")
    String telefon,
    
    Gen gen
) {
}
