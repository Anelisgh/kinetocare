package com.example.servicii_service.mapper;

import com.example.servicii_service.dto.ServiciuDTO;
import com.example.servicii_service.entity.Serviciu;
import org.springframework.stereotype.Component;

@Component
public class ServiciuMapper {

    public ServiciuDTO toDto(Serviciu serviciu) {
        if (serviciu == null) {
            return null;
        }

        // daca TipServiciu este null, returnam "Necunoscut"
        String numeTip = (serviciu.getTipServiciu() != null)
                ? serviciu.getTipServiciu().getNume()
                : "Necunoscut";

        return ServiciuDTO.builder()
                .id(serviciu.getId())
                .nume(numeTip) // luam numele din relatia many-to-one
                .pret(serviciu.getPret())
                .durataMinute(serviciu.getDurataMinute())
                .build();
    }
}
