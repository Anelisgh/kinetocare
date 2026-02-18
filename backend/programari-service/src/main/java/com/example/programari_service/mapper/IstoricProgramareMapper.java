package com.example.programari_service.mapper;

import com.example.programari_service.dto.IstoricProgramareDTO;
import com.example.programari_service.dto.DetaliiJurnalDTO;
import com.example.programari_service.entity.Evaluare;
import com.example.programari_service.entity.Programare;
import org.springframework.stereotype.Component;

@Component
public class IstoricProgramareMapper {

    public IstoricProgramareDTO toDTO(Programare p, String numeTerapeut, String numeLocatie, Evaluare evaluare, String serviciuRecomandat, DetaliiJurnalDTO detaliiJurnal) {
        if (p == null) return null;

        String diagnostic = null;
        Integer sedinteRecomandate = null;
        String observatii = null;

        if (evaluare != null) {
            diagnostic = evaluare.getDiagnostic();
            sedinteRecomandate = evaluare.getSedinteRecomandate();
            observatii = evaluare.getObservatii();
        }

        return IstoricProgramareDTO.builder()
                .id(p.getId())
                .data(p.getData())
                .oraInceput(p.getOraInceput())
                .oraSfarsit(p.getOraSfarsit())
                .tipServiciu(p.getTipServiciu())
                .pret(p.getPret())
                .status(p.getStatus())
                .numeTerapeut(numeTerapeut)
                .terapeutId(p.getTerapeutId())
                .numeLocatie(numeLocatie)
                .areEvaluare(p.getAreEvaluare())
                .areJurnal(p.getAreJurnal())
                .motivAnulare(p.getMotivAnulare() != null ? p.getMotivAnulare().getDisplayName() : null)
                .detaliiJurnal(detaliiJurnal)
                .diagnostic(diagnostic)
                .sedinteRecomandate(sedinteRecomandate)
                .observatii(observatii)
                .serviciuRecomandat(serviciuRecomandat)
                .build();
    }
}
