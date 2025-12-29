package com.example.programari_service.mapper;

import com.example.programari_service.dto.UserNumeDTO;
import com.example.programari_service.dto.UserDisplayCalendarDTO;
import org.springframework.stereotype.Component;

@Component
public class PacientMapper {
    public UserNumeDTO toPacientNumeDTO(UserDisplayCalendarDTO userDTO, Long userId) {
        if (userDTO == null) {
            return null;
        }

        return UserNumeDTO.builder()
                .id(userId)
                .nume(userDTO.getNume())
                .prenume(userDTO.getPrenume())
                .build();
    }
}
