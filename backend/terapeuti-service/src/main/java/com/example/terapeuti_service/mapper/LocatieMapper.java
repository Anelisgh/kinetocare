package com.example.terapeuti_service.mapper;

import com.example.terapeuti_service.dto.DisponibilitateDTO;
import com.example.terapeuti_service.dto.LocatieDTO;
import com.example.terapeuti_service.dto.LocatieDisponibilaDTO;
import com.example.terapeuti_service.entity.Locatie;
import org.springframework.stereotype.Component;

@Component
public class LocatieMapper {

    public LocatieDTO toDTO(Locatie entity) {
        if (entity == null) {
            return null;
        }

        return LocatieDTO.builder()
                .id(entity.getId())
                .nume(entity.getNume())
                .adresa(entity.getAdresa())
                .oras(entity.getOras())
                .judet(entity.getJudet())
                .codPostal(entity.getCodPostal())
                .telefon(entity.getTelefon())
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
    public Locatie toEntity(LocatieDTO dto) {
        return Locatie.builder()
                .nume(dto.getNume())
                .adresa(dto.getAdresa())
                .oras(dto.getOras())
                .judet(dto.getJudet())
                .codPostal(dto.getCodPostal())
                .telefon(dto.getTelefon())
                .active(true)
                .build();
    }

    public void updateEntityFromDTO(Locatie locatie, LocatieDTO dto) {
        if (dto.getNume() != null) locatie.setNume(dto.getNume());
        if (dto.getAdresa() != null) locatie.setAdresa(dto.getAdresa());
        if (dto.getOras() != null) locatie.setOras(dto.getOras());
        if (dto.getJudet() != null) locatie.setJudet(dto.getJudet());
        if (dto.getCodPostal() != null) locatie.setCodPostal(dto.getCodPostal());
        if (dto.getTelefon() != null) locatie.setTelefon(dto.getTelefon());
    }

    public LocatieDisponibilaDTO toDisponibilaDTO(Locatie locatie) {
        if (locatie == null) return null;

        return LocatieDisponibilaDTO.builder()
                .id(locatie.getId())
                .nume(locatie.getNume())
                .adresa(locatie.getAdresa())
                .oras(locatie.getOras())
                .judet(locatie.getJudet())
                .build();
    }

    public LocatieDisponibilaDTO fromDisponibilitateDTO(DisponibilitateDTO disp) {
        if (disp == null) return null;

        return LocatieDisponibilaDTO.builder()
                .id(disp.getLocatieId())
                .nume(disp.getLocatieNume())
                .adresa(disp.getLocatieAdresa())
                .oras(disp.getLocatieOras())
                .build();
    }
}
