package com.example.servicii_service.mapper;

import com.example.servicii_service.dto.ServiciuDTO;
import com.example.servicii_service.entity.Serviciu;
import org.springframework.stereotype.Component;

import com.example.servicii_service.dto.ServiciuAdminDTO;
import com.example.servicii_service.entity.TipServiciu;

@Component
public class ServiciuMapper {

    public ServiciuDTO toDto(Serviciu serviciu) {
        if (serviciu == null) {
            return null;
        }

        // Construim numele complet: Tip + Specific
        // ex: "Kinetoterapie - Sedinta Scurta" sau doar "Kinetoterapie"
        String numeComplet = (serviciu.getTipServiciu() != null)
                ? serviciu.getTipServiciu().getNume()
                : "Necunoscut";

        if (serviciu.getNume() != null && !serviciu.getNume().trim().isEmpty()) {
            numeComplet += " - " + serviciu.getNume();
        }

        return ServiciuDTO.builder()
                .id(serviciu.getId())
                .nume(numeComplet)
                .pret(serviciu.getPret())
                .durataMinute(serviciu.getDurataMinute())
                .build();
    }

    public ServiciuAdminDTO toAdminDto(Serviciu serviciu) {
        if (serviciu == null) return null;

        return ServiciuAdminDTO.builder()
                .id(serviciu.getId())
                .tipServiciuId(serviciu.getTipServiciu() != null ? serviciu.getTipServiciu().getId() : null)
                .numeTip(serviciu.getTipServiciu() != null ? serviciu.getTipServiciu().getNume() : null)
                .nume(serviciu.getNume())
                .pret(serviciu.getPret())
                .durataMinute(serviciu.getDurataMinute())
                .active(serviciu.getActive())
                .build();
    }

    public Serviciu toEntity(ServiciuAdminDTO dto, TipServiciu tipServiciu) {
        if (dto == null) return null;

        return Serviciu.builder()
                .tipServiciu(tipServiciu)
                .nume(dto.getNume())
                .pret(dto.getPret())
                .durataMinute(dto.getDurataMinute())
                .active(dto.getActive() != null ? dto.getActive() : true)
                .build();
    }

    public void updateEntityFromDto(ServiciuAdminDTO dto, Serviciu entity, TipServiciu tipServiciu) {
        if (dto == null || entity == null) return;
        
        if (tipServiciu != null) {
            entity.setTipServiciu(tipServiciu);
        }
        
        if (dto.getNume() != null) entity.setNume(dto.getNume());
        
        if (dto.getPret() != null) entity.setPret(dto.getPret());
        if (dto.getDurataMinute() != null) entity.setDurataMinute(dto.getDurataMinute());
        if (dto.getActive() != null) entity.setActive(dto.getActive());
    }
}
