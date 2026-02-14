package com.example.user_service.dto;

import com.example.user_service.entity.Gen;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// pentru /profile
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String nume;
    private String prenume;
    private String email;
    private String telefon;
    private Gen gen;
}
