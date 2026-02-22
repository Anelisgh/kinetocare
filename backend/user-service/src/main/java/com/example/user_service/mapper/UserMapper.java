package com.example.user_service.mapper;

import com.example.user_service.dto.AdminUserDTO;
import com.example.user_service.dto.UpdateUserDTO;
import com.example.user_service.dto.UserDTO;
import com.example.user_service.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDTO toDTO(User user) {
        return new UserDTO(
            user.getId(),
            user.getKeycloakId(),
            user.getNume(),
            user.getPrenume(),
            user.getEmail(),
            user.getTelefon(),
            user.getGen()
        );
    }

    // pentru admin - listing useri
    public AdminUserDTO toAdminDTO(User user) {
        return new AdminUserDTO(
            user.getId(),
            user.getNume(),
            user.getPrenume(),
            user.getEmail(),
            user.getTelefon(),
            user.getRole().name(),
            user.getActive(),
            user.getCreatedAt()
        );
    }

    public void updateEntity(User user, UpdateUserDTO updateDTO) {
        if (updateDTO.nume() != null && !updateDTO.nume().isEmpty()) {
            user.setNume(updateDTO.nume());
        }
        if (updateDTO.prenume() != null && !updateDTO.prenume().isEmpty()) {
            user.setPrenume(updateDTO.prenume());
        }
        if (updateDTO.email() != null && !updateDTO.email().isEmpty()) {
            user.setEmail(updateDTO.email());
        }
        if (updateDTO.telefon() != null && !updateDTO.telefon().isEmpty()) {
            user.setTelefon(updateDTO.telefon());
        }
        if (updateDTO.gen() != null){
            user.setGen(updateDTO.gen());
        }
    }
}
