package com.example.user_service.dto;

import com.example.user_service.entity.Gen;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateUserDTO {
    @Size(min = 2, max = 100, message = "Numele trebuie să aibă între 2 și 100 caractere")
    private String nume;
    @Size(min = 2, max = 100, message = "Prenumele trebuie să aibă între 2 și 100 caractere")
    private String prenume;
    @Email(message = "Email invalid")
    private String email;
    @Pattern(regexp = "^07[0-9]{8}$", message = "Număr de telefon invalid")
    private String telefon;
    private Gen gen;
}
