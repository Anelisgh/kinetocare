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
    public Long id;
    public String nume;
    public String prenume;
    public String email;
    private String telefon;
    private Gen gen;
}
