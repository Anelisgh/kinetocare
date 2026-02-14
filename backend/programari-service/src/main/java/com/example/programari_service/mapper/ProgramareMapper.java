package com.example.programari_service.mapper;

import com.example.programari_service.dto.CalendarProgramareDTO;
import com.example.programari_service.dto.CreeazaProgramareRequest;
import com.example.programari_service.dto.DetaliiServiciuDTO;
import com.example.programari_service.dto.ProgramareJurnalDTO;
import com.example.programari_service.dto.ProgramareResponseDTO;
import com.example.programari_service.dto.UrmatoareaProgramareDTO;
import com.example.programari_service.entity.Programare;
import com.example.programari_service.entity.StatusProgramare;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
public class ProgramareMapper {
    public UrmatoareaProgramareDTO toUrmatoareaProgramareDTO(Programare programare) {
        if (programare == null)
            return null;

        return UrmatoareaProgramareDTO.builder()
                .id(programare.getId())
                .serviciuId(programare.getServiciuId())
                .tipServiciu(programare.getTipServiciu())
                .pret(programare.getPret())
                .data(programare.getData())
                .oraInceput(programare.getOraInceput())
                .oraSfarsit(programare.getOraSfarsit())
                .locatieId(programare.getLocatieId())
                .terapeutId(programare.getTerapeutId())
                .build();
    }

    public Programare toEntity(CreeazaProgramareRequest request,
            DetaliiServiciuDTO serviciuInfo,
            LocalTime oraSfarsitCalculata,
            boolean isPrimaIntalnire) {

        Programare programare = new Programare();

        programare.setPacientId(request.getPacientId());
        programare.setTerapeutId(request.getTerapeutId());
        programare.setLocatieId(request.getLocatieId());
        programare.setData(request.getData());
        programare.setOraInceput(request.getOraInceput());
        // calculate
        programare.setOraSfarsit(oraSfarsitCalculata);
        programare.setPrimaIntalnire(isPrimaIntalnire);

        // din feign
        programare.setServiciuId(serviciuInfo.getId());
        programare.setTipServiciu(serviciuInfo.getNume());
        programare.setPret(serviciuInfo.getPret());
        programare.setDurataMinute(serviciuInfo.getDurataMinute());

        programare.setStatus(StatusProgramare.PROGRAMATA);
        programare.setAreEvaluare(false);
        programare.setAreJurnal(false);

        return programare;
    }

    public CalendarProgramareDTO toCalendarDTO(Programare programare,
                                               String numePacient,
                                               String telefonPacient,
                                               String numeLocatie) {
        if (programare == null) return null;

        return CalendarProgramareDTO.builder()
                .id(programare.getId())
                .title(numePacient)
                .start(java.time.LocalDateTime.of(programare.getData(), programare.getOraInceput()))
                .end(java.time.LocalDateTime.of(programare.getData(), programare.getOraSfarsit()))
                .numeLocatie(numeLocatie)
                .tipServiciu(programare.getTipServiciu())
                .status(programare.getStatus())
                .motivAnulare(programare.getMotivAnulare())
                .primaIntalnire(Boolean.TRUE.equals(programare.getPrimaIntalnire()))
                .telefonPacient(telefonPacient)
                .build();
    }

    // conversie Programare + date externe -> ProgramareJurnalDTO (pentru jurnal pacient si detalii programare)
    public ProgramareJurnalDTO toProgramareJurnalDTO(Programare programare,
                                                      String numeTerapeut,
                                                      String numeLocatie) {
        if (programare == null) return null;

        return ProgramareJurnalDTO.builder()
                .id(programare.getId())
                .tipServiciu(programare.getTipServiciu())
                .data(programare.getData())
                .ora(programare.getOraInceput())
                .numeTerapeut(numeTerapeut)
                .numeLocatie(numeLocatie)
                .build();
    }

    public ProgramareResponseDTO toResponseDTO(Programare programare) {
        if (programare == null) return null;

        return ProgramareResponseDTO.builder()
                .id(programare.getId())
                .pacientId(programare.getPacientId())
                .terapeutId(programare.getTerapeutId())
                .locatieId(programare.getLocatieId())
                .serviciuId(programare.getServiciuId())
                .tipServiciu(programare.getTipServiciu())
                .pret(programare.getPret())
                .durataMinute(programare.getDurataMinute())
                .primaIntalnire(programare.getPrimaIntalnire())
                .data(programare.getData())
                .oraInceput(programare.getOraInceput())
                .oraSfarsit(programare.getOraSfarsit())
                .status(programare.getStatus())
                .build();
    }
}
