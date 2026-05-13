package com.example.programari_service.mapper;

import com.example.programari_service.dto.UserDisplayCalendarDTO;
import com.example.programari_service.dto.UserNumeDTO;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-13T13:22:10+0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class PacientMapperImpl implements PacientMapper {

    @Override
    public UserNumeDTO toPacientNumeDTO(UserDisplayCalendarDTO userDTO, String keycloakId) {
        if ( userDTO == null && keycloakId == null ) {
            return null;
        }

        String nume = null;
        String prenume = null;
        if ( userDTO != null ) {
            nume = userDTO.nume();
            prenume = userDTO.prenume();
        }
        String keycloakId1 = null;
        keycloakId1 = keycloakId;

        UserNumeDTO userNumeDTO = new UserNumeDTO( keycloakId1, nume, prenume );

        return userNumeDTO;
    }
}
