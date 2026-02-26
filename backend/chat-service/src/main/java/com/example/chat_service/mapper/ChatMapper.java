package com.example.chat_service.mapper;

import com.example.chat_service.dto.ConversatieDTO;
import com.example.chat_service.dto.MesajDTO;
import com.example.chat_service.entity.Conversatie;
import com.example.chat_service.entity.Mesaj;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChatMapper {

    @Mapping(target = "expeditorKeycloakId", source = "expeditorKeycloakId")
    MesajDTO toMesajDTO(Mesaj mesaj);

    Mesaj toMesaj(MesajDTO mesajDTO);

    @Mapping(target = "ultimulMesaj", ignore = true)
    ConversatieDTO toConversatieDTO(Conversatie conversatie);

    Conversatie toConversatie(ConversatieDTO conversatieDTO);

    default ConversatieDTO toConversatieDTOWithMesaj(Conversatie conversatie, Mesaj ultimulMesaj) {
        if (conversatie == null) {
            return null;
        }
        return new ConversatieDTO(
                conversatie.getId(),
                conversatie.getPacientKeycloakId(),
                conversatie.getTerapeutKeycloakId(),
                conversatie.getUltimulMesajLa(),
                conversatie.getCreatedAt(),
                conversatie.getUpdatedAt(),
                ultimulMesaj != null ? toMesajDTO(ultimulMesaj) : null
        );
    }
}
