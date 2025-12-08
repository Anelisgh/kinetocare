package com.example.terapeuti_service.mapper;

import com.example.terapeuti_service.dto.DisponibilitateDTO;
import com.example.terapeuti_service.dto.CreateDisponibilitateDTO;
import com.example.terapeuti_service.entity.DisponibilitateTerapeut;
import com.example.terapeuti_service.entity.Locatie;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Locale;

@Component
public class DisponibilitateMapper {

    public DisponibilitateDTO toDTO(DisponibilitateTerapeut entity, Locatie locatie) {
        if (entity == null) {
            return null;
        }

        String ziNume = getZiSaptamanaNume(entity.getZiSaptamana());

        return DisponibilitateDTO.builder()
                .id(entity.getId())
                .terapeutId(entity.getTerapeutId())
                .ziSaptamana(entity.getZiSaptamana())
                .ziSaptamanaNume(ziNume)
                .locatieId(entity.getLocatieId())
                .locatieNume(locatie != null ? locatie.getNume() : null)
                .locatieAdresa(locatie != null ? locatie.getAdresa() : null)
                .locatieOras(locatie != null ? locatie.getOras() : null)
                .oraInceput(entity.getOraInceput())
                .oraSfarsit(entity.getOraSfarsit())
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public DisponibilitateTerapeut toEntity(CreateDisponibilitateDTO dto, Long terapeutId) {
        if (dto == null) {
            return null;
        }

        return DisponibilitateTerapeut.builder()
                .terapeutId(terapeutId)
                .ziSaptamana(dto.getZiSaptamana())
                .locatieId(dto.getLocatieId())
                .oraInceput(dto.getOraInceput())
                .oraSfarsit(dto.getOraSfarsit())
                .active(true)
                .build();
    }

    private String getZiSaptamanaNume(Integer zi) {
        if (zi == null || zi < 1 || zi > 7) {
            return "Necunoscut";
        }
        DayOfWeek dayOfWeek = DayOfWeek.of(zi);
        return dayOfWeek.getDisplayName(TextStyle.FULL, Locale.of("ro"));
    }
}
