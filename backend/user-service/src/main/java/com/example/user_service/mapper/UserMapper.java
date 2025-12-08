package com.example.user_service.mapper;

import com.example.user_service.dto.UpdateUserDTO;
import com.example.user_service.dto.UserDTO;
import com.example.user_service.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setNume(user.getNume());
        dto.setPrenume(user.getPrenume());
        dto.setEmail(user.getEmail());
        dto.setTelefon(user.getTelefon());
        dto.setGen(user.getGen());
        return dto;
    }

    public void updateEntity(User user, UpdateUserDTO updateDTO) {
        if (updateDTO.getNume() != null && !updateDTO.getNume().isEmpty()) {
            user.setNume(updateDTO.getNume());
        }
        if (updateDTO.getPrenume() != null && !updateDTO.getPrenume().isEmpty()) {
            user.setPrenume(updateDTO.getPrenume());
        }
        if (updateDTO.getEmail() != null && !updateDTO.getEmail().isEmpty()) {
            user.setEmail(updateDTO.getEmail());
        }
        if (updateDTO.getTelefon() != null && !updateDTO.getTelefon().isEmpty()) {
            user.setTelefon(updateDTO.getTelefon());
        }
        if (updateDTO.getGen() != null){
            user.setGen(updateDTO.getGen());
        }
    }
}
