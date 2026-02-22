package com.example.pacienti_service.mapper;

import com.example.pacienti_service.dto.PacientCompleteProfileRequest;
import com.example.pacienti_service.dto.PacientRequest;
import com.example.pacienti_service.dto.PacientResponse;
import com.example.pacienti_service.entity.Pacient;
import com.example.pacienti_service.entity.FaceSport;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PacientMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orasPreferat", ignore = true)
    @Mapping(target = "locatiePreferataId", ignore = true)
    @Mapping(target = "terapeutKeycloakId", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "keycloakId", source = "keycloakId")
    Pacient toEntity(PacientCompleteProfileRequest request, String keycloakId);

    PacientResponse toResponse(Pacient pacient);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "keycloakId", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(@MappingTarget Pacient pacient, PacientRequest request);

    @AfterMapping
    default void handleDetaliiSport(@MappingTarget Pacient pacient) {
        if (pacient.getFaceSport() == FaceSport.NU) {
            pacient.setDetaliiSport(null);
        }
    }
}