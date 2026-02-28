package com.example.programari_service.mapper;

import com.example.programari_service.entity.RelatiePacientTerapeut;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;

@Mapper(componentModel = "spring")
public interface RelatieMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "activa", constant = "true")
    @Mapping(target = "dataSfarsit", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    RelatiePacientTerapeut toEntity(String pacientKeycloakId, String terapeutKeycloakId, LocalDate dataInceput);
}
