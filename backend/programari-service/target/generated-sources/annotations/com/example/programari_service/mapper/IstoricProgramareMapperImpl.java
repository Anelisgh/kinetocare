package com.example.programari_service.mapper;

import com.example.programari_service.dto.DetaliiJurnalDTO;
import com.example.programari_service.dto.IstoricProgramareDTO;
import com.example.programari_service.entity.Evaluare;
import com.example.programari_service.entity.Programare;
import com.example.programari_service.entity.StatusProgramare;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-13T13:22:10+0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class IstoricProgramareMapperImpl implements IstoricProgramareMapper {

    @Override
    public IstoricProgramareDTO toDTO(Programare p, String numeTerapeut, String numeLocatie, Evaluare evaluare, String serviciuRecomandat, DetaliiJurnalDTO detaliiJurnal) {
        if ( p == null && numeTerapeut == null && numeLocatie == null && evaluare == null && serviciuRecomandat == null && detaliiJurnal == null ) {
            return null;
        }

        Long id = null;
        LocalDate data = null;
        LocalTime oraInceput = null;
        LocalTime oraSfarsit = null;
        String tipServiciu = null;
        BigDecimal pret = null;
        StatusProgramare status = null;
        String terapeutKeycloakId = null;
        Boolean areEvaluare = null;
        Boolean areJurnal = null;
        if ( p != null ) {
            id = p.getId();
            data = p.getData();
            oraInceput = p.getOraInceput();
            oraSfarsit = p.getOraSfarsit();
            tipServiciu = p.getTipServiciu();
            pret = p.getPret();
            status = p.getStatus();
            terapeutKeycloakId = p.getTerapeutKeycloakId();
            areEvaluare = p.getAreEvaluare();
            areJurnal = p.getAreJurnal();
        }
        String diagnostic = null;
        Integer sedinteRecomandate = null;
        String observatii = null;
        if ( evaluare != null ) {
            diagnostic = evaluare.getDiagnostic();
            sedinteRecomandate = evaluare.getSedinteRecomandate();
            observatii = evaluare.getObservatii();
        }
        String numeTerapeut1 = null;
        numeTerapeut1 = numeTerapeut;
        String numeLocatie1 = null;
        numeLocatie1 = numeLocatie;
        String serviciuRecomandat1 = null;
        serviciuRecomandat1 = serviciuRecomandat;
        DetaliiJurnalDTO detaliiJurnal1 = null;
        detaliiJurnal1 = detaliiJurnal;

        String motivAnulare = p.getMotivAnulare() != null ? p.getMotivAnulare().getDisplayName() : null;

        IstoricProgramareDTO istoricProgramareDTO = new IstoricProgramareDTO( id, data, oraInceput, oraSfarsit, tipServiciu, pret, status, terapeutKeycloakId, numeTerapeut1, numeLocatie1, areEvaluare, areJurnal, motivAnulare, detaliiJurnal1, diagnostic, sedinteRecomandate, observatii, serviciuRecomandat1 );

        return istoricProgramareDTO;
    }
}
