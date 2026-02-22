package com.example.servicii_service.mapper;

import com.example.servicii_service.dto.TipServiciuDTO;
import com.example.servicii_service.entity.TipServiciu;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TipServiciuMapper {

    TipServiciuDTO toDto(TipServiciu tip);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", source = "active", defaultValue = "true")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    TipServiciu toEntity(TipServiciuDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(TipServiciuDTO dto, @MappingTarget TipServiciu entity);
}
