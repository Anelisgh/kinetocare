package com.example.servicii_service.mapper;

import com.example.servicii_service.dto.TipServiciuDTO;
import com.example.servicii_service.entity.TipServiciu;
import org.springframework.stereotype.Component;

@Component
public class TipServiciuMapper {

    public TipServiciuDTO toDto(TipServiciu tip) {
        if (tip == null) return null;
        return TipServiciuDTO.builder()
                .id(tip.getId())
                .nume(tip.getNume())
                .descriere(tip.getDescriere())
                .active(tip.getActive())
                .build();
    }

    public TipServiciu toEntity(TipServiciuDTO dto) {
        if (dto == null) return null;
        return TipServiciu.builder()
                .nume(dto.getNume())
                .descriere(dto.getDescriere())
                .active(dto.getActive() != null ? dto.getActive() : true)
                .build();
    }

    public void updateEntityFromDto(TipServiciuDTO dto, TipServiciu entity) {
        if (dto == null || entity == null) return;
        if (dto.getNume() != null) entity.setNume(dto.getNume());
        if (dto.getDescriere() != null) entity.setDescriere(dto.getDescriere());
        if (dto.getActive() != null) entity.setActive(dto.getActive());
    }
}
