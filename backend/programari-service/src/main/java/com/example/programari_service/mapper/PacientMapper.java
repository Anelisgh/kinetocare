package com.example.programari_service.mapper;

import com.example.programari_service.dto.UserNumeDTO;
import com.example.programari_service.dto.UserDisplayCalendarDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PacientMapper {

    @Mapping(target = "keycloakId", source = "keycloakId")
    @Mapping(target = "nume", source = "userDTO.nume")
    @Mapping(target = "prenume", source = "userDTO.prenume")
    UserNumeDTO toPacientNumeDTO(UserDisplayCalendarDTO userDTO, String keycloakId);
}
