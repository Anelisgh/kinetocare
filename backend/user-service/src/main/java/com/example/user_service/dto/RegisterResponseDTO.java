package com.example.user_service.dto;

import com.example.user_service.entity.Gen;
import com.example.user_service.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponseDTO {
    private String keycloakId;
    private String email;
    private String nume;
    private String prenume;
    private UserRole role;
    private Gen gen;
    private String message;
}