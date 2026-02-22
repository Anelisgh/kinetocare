package com.example.terapeuti_service.mapper;

import com.example.terapeuti_service.dto.*;
import com.example.terapeuti_service.entity.Terapeut;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.BeanMapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TerapeutMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "specializare", ignore = true)
    @Mapping(target = "pozaProfil", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", constant = "true")
    Terapeut toNewEntity(String keycloakId);

    @Mapping(target = "disponibilitati", ignore = true)
    @Mapping(target = "concedii", ignore = true)
    @Mapping(target = "locatiiDisponibile", ignore = true)
    @Mapping(target = "profileIncomplete", ignore = true)
    TerapeutDTO toDTO(Terapeut terapeut);

    @Mapping(target = "keycloakId", source = "terapeut.keycloakId")
    @Mapping(target = "pozaProfil", source = "terapeut.pozaProfil")
    @Mapping(target = "specializare", source = "terapeut.specializare")
    @Mapping(target = "disponibilitati", source = "disponibilitati")
    @Mapping(target = "locatiiDisponibile", source = "locatiiUnice")
    TerapeutDetaliDTO toDetaliDTO(Terapeut terapeut, List<DisponibilitateDTO> disponibilitati, List<LocatieDisponibilaDTO> locatiiUnice);

    @Mapping(target = "keycloakId", source = "terapeut.keycloakId")
    @Mapping(target = "pozaProfil", source = "terapeut.pozaProfil")
    @Mapping(target = "specializare", source = "terapeut.specializare")
    @Mapping(target = "locatiiDisponibile", source = "locatiiDisp")
    TerapeutSearchDTO toSearchDTO(Terapeut terapeut, List<LocatieDisponibilaDTO> locatiiDisp);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "keycloakId", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(@MappingTarget Terapeut terapeut, UpdateTerapeutDTO dto);
}
