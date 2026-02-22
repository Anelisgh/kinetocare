package com.example.terapeuti_service.mapper;

import com.example.terapeuti_service.dto.DisponibilitateDTO;
import com.example.terapeuti_service.dto.LocatieDTO;
import com.example.terapeuti_service.dto.LocatieDisponibilaDTO;
import com.example.terapeuti_service.entity.Locatie;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.BeanMapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface LocatieMapper {

    LocatieDTO toDTO(Locatie entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", constant = "true")
    Locatie toEntity(LocatieDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(@MappingTarget Locatie locatie, LocatieDTO dto);

    LocatieDisponibilaDTO toDisponibilaDTO(Locatie locatie);

    @Mapping(target = "id", source = "locatieId")
    @Mapping(target = "nume", source = "locatieNume")
    @Mapping(target = "adresa", source = "locatieAdresa")
    @Mapping(target = "oras", source = "locatieOras")
    @Mapping(target = "judet", ignore = true)
    @Mapping(target = "active", ignore = true)
    LocatieDisponibilaDTO fromDisponibilitateDTO(DisponibilitateDTO disp);
}
