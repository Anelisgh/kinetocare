package com.example.terapeuti_service.mapper;

import com.example.terapeuti_service.dto.DisponibilitateDTO;
import com.example.terapeuti_service.dto.CreateDisponibilitateDTO;
import com.example.terapeuti_service.entity.DisponibilitateTerapeut;
import com.example.terapeuti_service.entity.Locatie;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Locale;

@Mapper(componentModel = "spring")
public interface DisponibilitateMapper {

    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "terapeutId", source = "entity.terapeutId")
    @Mapping(target = "locatieId", source = "entity.locatieId")
    @Mapping(target = "oraInceput", source = "entity.oraInceput")
    @Mapping(target = "oraSfarsit", source = "entity.oraSfarsit")
    @Mapping(target = "active", source = "entity.active")
    @Mapping(target = "createdAt", source = "entity.createdAt")
    @Mapping(target = "updatedAt", source = "entity.updatedAt")
    @Mapping(target = "ziSaptamana", source = "entity.ziSaptamana")
    @Mapping(target = "ziSaptamanaNume", expression = "java(getZiSaptamanaNume(entity.getZiSaptamana()))")
    @Mapping(target = "locatieNume", source = "locatie.nume")
    @Mapping(target = "locatieAdresa", source = "locatie.adresa")
    @Mapping(target = "locatieOras", source = "locatie.oras")
    DisponibilitateDTO toDTO(DisponibilitateTerapeut entity, Locatie locatie);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", constant = "true")
    DisponibilitateTerapeut toEntity(CreateDisponibilitateDTO dto, Long terapeutId);

    default String getZiSaptamanaNume(Integer zi) {
    if (zi == null || zi < 1 || zi > 7) {
        return "Nespecificat";
    }
    return DayOfWeek.of(zi)
            .getDisplayName(TextStyle.FULL, Locale.of("ro"))
            .substring(0, 1).toUpperCase() + 
            DayOfWeek.of(zi).getDisplayName(TextStyle.FULL, Locale.of("ro")).substring(1);
}
}
