package com.example.terapeuti_service.mapper;

import com.example.terapeuti_service.dto.ConcediuDTO;
import com.example.terapeuti_service.dto.CreateConcediuDTO;
import com.example.terapeuti_service.entity.ConcediuTerapeut;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ConcediuMapper {

    ConcediuDTO toDTO(ConcediuTerapeut entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    ConcediuTerapeut toEntity(CreateConcediuDTO dto, Long terapeutId);
}
