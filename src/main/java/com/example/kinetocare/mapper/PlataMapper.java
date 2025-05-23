package com.example.kinetocare.mapper;

import com.example.kinetocare.domain.Plata;
import com.example.kinetocare.dto.PlataDTO;
import org.springframework.stereotype.Component;

@Component
public class PlataMapper {

    public PlataDTO toDto(Plata plata) {
        return PlataDTO.builder()
                .dataProgramare(plata.getProgramare() != null ?
                        plata.getProgramare().getData() : null)
                .serviciu(plata.getProgramare() != null &&
                        plata.getProgramare().getServiciu() != null ?
                        plata.getProgramare().getServiciu().getTipServiciu().getDisplayName() : "Necunoscut")
                .suma(plata.getSuma())
                .starePlata(plata.getStarePlata().getDisplayName())
                .build();
    }
}
