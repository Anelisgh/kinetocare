package com.example.programari_service.mapper;

import com.example.programari_service.dto.CalendarProgramareDTO;
import com.example.programari_service.dto.CreeazaProgramareRequest;
import com.example.programari_service.dto.DetaliiServiciuDTO;
import com.example.programari_service.dto.ProgramareJurnalDTO;
import com.example.programari_service.dto.ProgramareResponseDTO;
import com.example.programari_service.dto.UrmatoareaProgramareDTO;
import com.example.programari_service.entity.MotivAnulare;
import com.example.programari_service.entity.Programare;
import com.example.programari_service.entity.StatusProgramare;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-13T13:22:10+0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class ProgramareMapperImpl implements ProgramareMapper {

    @Override
    public UrmatoareaProgramareDTO toUrmatoareaProgramareDTO(Programare programare) {
        if ( programare == null ) {
            return null;
        }

        Long id = null;
        Long serviciuId = null;
        String tipServiciu = null;
        BigDecimal pret = null;
        LocalDate data = null;
        LocalTime oraInceput = null;
        LocalTime oraSfarsit = null;
        Long locatieId = null;
        String terapeutKeycloakId = null;

        id = programare.getId();
        serviciuId = programare.getServiciuId();
        tipServiciu = programare.getTipServiciu();
        pret = programare.getPret();
        data = programare.getData();
        oraInceput = programare.getOraInceput();
        oraSfarsit = programare.getOraSfarsit();
        locatieId = programare.getLocatieId();
        terapeutKeycloakId = programare.getTerapeutKeycloakId();

        UrmatoareaProgramareDTO urmatoareaProgramareDTO = new UrmatoareaProgramareDTO( id, serviciuId, tipServiciu, pret, data, oraInceput, oraSfarsit, locatieId, terapeutKeycloakId );

        return urmatoareaProgramareDTO;
    }

    @Override
    public Programare toEntity(CreeazaProgramareRequest request, DetaliiServiciuDTO serviciuInfo, LocalTime oraSfarsitCalculata, boolean isPrimaIntalnire) {
        if ( request == null && serviciuInfo == null && oraSfarsitCalculata == null ) {
            return null;
        }

        Programare.ProgramareBuilder programare = Programare.builder();

        if ( request != null ) {
            programare.pacientKeycloakId( request.pacientKeycloakId() );
            programare.terapeutKeycloakId( request.terapeutKeycloakId() );
            programare.locatieId( request.locatieId() );
            programare.data( request.data() );
            programare.oraInceput( request.oraInceput() );
        }
        if ( serviciuInfo != null ) {
            programare.serviciuId( serviciuInfo.id() );
            programare.tipServiciu( serviciuInfo.nume() );
            programare.pret( serviciuInfo.pret() );
            programare.durataMinute( serviciuInfo.durataMinute() );
        }
        programare.oraSfarsit( oraSfarsitCalculata );
        programare.primaIntalnire( isPrimaIntalnire );
        programare.status( StatusProgramare.PROGRAMATA );
        programare.areEvaluare( false );
        programare.areJurnal( false );

        return programare.build();
    }

    @Override
    public CalendarProgramareDTO toCalendarDTO(Programare programare, String numePacient, String telefonPacient, String numeLocatie) {
        if ( programare == null && numePacient == null && telefonPacient == null && numeLocatie == null ) {
            return null;
        }

        Long id = null;
        String tipServiciu = null;
        StatusProgramare status = null;
        MotivAnulare motivAnulare = null;
        if ( programare != null ) {
            id = programare.getId();
            tipServiciu = programare.getTipServiciu();
            status = programare.getStatus();
            motivAnulare = programare.getMotivAnulare();
        }
        String title = null;
        title = numePacient;
        String telefonPacient1 = null;
        telefonPacient1 = telefonPacient;
        String numeLocatie1 = null;
        numeLocatie1 = numeLocatie;

        boolean primaIntalnire = Boolean.TRUE.equals(programare.getPrimaIntalnire());
        LocalDateTime start = java.time.LocalDateTime.of(programare.getData(), programare.getOraInceput());
        LocalDateTime end = java.time.LocalDateTime.of(programare.getData(), programare.getOraSfarsit());

        CalendarProgramareDTO calendarProgramareDTO = new CalendarProgramareDTO( id, title, start, end, numeLocatie1, tipServiciu, status, motivAnulare, primaIntalnire, telefonPacient1 );

        return calendarProgramareDTO;
    }

    @Override
    public ProgramareJurnalDTO toProgramareJurnalDTO(Programare programare, String numeTerapeut, String terapeutKeycloakId, String numeLocatie) {
        if ( programare == null && numeTerapeut == null && terapeutKeycloakId == null && numeLocatie == null ) {
            return null;
        }

        Long id = null;
        String tipServiciu = null;
        LocalDate data = null;
        LocalTime ora = null;
        String terapeutKeycloakId1 = null;
        if ( programare != null ) {
            id = programare.getId();
            tipServiciu = programare.getTipServiciu();
            data = programare.getData();
            ora = programare.getOraInceput();
            terapeutKeycloakId1 = programare.getTerapeutKeycloakId();
        }
        String numeTerapeut1 = null;
        numeTerapeut1 = numeTerapeut;
        String numeLocatie1 = null;
        numeLocatie1 = numeLocatie;

        ProgramareJurnalDTO programareJurnalDTO = new ProgramareJurnalDTO( id, tipServiciu, data, ora, numeTerapeut1, terapeutKeycloakId1, numeLocatie1 );

        return programareJurnalDTO;
    }

    @Override
    public ProgramareResponseDTO toResponseDTO(Programare programare) {
        if ( programare == null ) {
            return null;
        }

        Long id = null;
        String pacientKeycloakId = null;
        String terapeutKeycloakId = null;
        Long locatieId = null;
        Long serviciuId = null;
        String tipServiciu = null;
        BigDecimal pret = null;
        Integer durataMinute = null;
        Boolean primaIntalnire = null;
        LocalDate data = null;
        LocalTime oraInceput = null;
        LocalTime oraSfarsit = null;
        StatusProgramare status = null;

        id = programare.getId();
        pacientKeycloakId = programare.getPacientKeycloakId();
        terapeutKeycloakId = programare.getTerapeutKeycloakId();
        locatieId = programare.getLocatieId();
        serviciuId = programare.getServiciuId();
        tipServiciu = programare.getTipServiciu();
        pret = programare.getPret();
        durataMinute = programare.getDurataMinute();
        primaIntalnire = programare.getPrimaIntalnire();
        data = programare.getData();
        oraInceput = programare.getOraInceput();
        oraSfarsit = programare.getOraSfarsit();
        status = programare.getStatus();

        ProgramareResponseDTO programareResponseDTO = new ProgramareResponseDTO( id, pacientKeycloakId, terapeutKeycloakId, locatieId, serviciuId, tipServiciu, pret, durataMinute, primaIntalnire, data, oraInceput, oraSfarsit, status );

        return programareResponseDTO;
    }
}
