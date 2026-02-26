package com.example.programari_service.mapper;

import com.example.programari_service.dto.CalendarProgramareDTO;
import com.example.programari_service.dto.CreeazaProgramareRequest;
import com.example.programari_service.dto.DetaliiServiciuDTO;
import com.example.programari_service.dto.ProgramareJurnalDTO;
import com.example.programari_service.dto.ProgramareResponseDTO;
import com.example.programari_service.dto.UrmatoareaProgramareDTO;
import com.example.programari_service.entity.Programare;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalTime;

@Mapper(componentModel = "spring")
public interface ProgramareMapper {

    UrmatoareaProgramareDTO toUrmatoareaProgramareDTO(Programare programare);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "motivAnulare", ignore = true)
    @Mapping(target = "pacientId", source = "request.pacientId")
    @Mapping(target = "terapeutId", source = "request.terapeutId")
    @Mapping(target = "locatieId", source = "request.locatieId")
    @Mapping(target = "data", source = "request.data")
    @Mapping(target = "oraInceput", source = "request.oraInceput")
    @Mapping(target = "oraSfarsit", source = "oraSfarsitCalculata")
    @Mapping(target = "primaIntalnire", source = "isPrimaIntalnire")
    @Mapping(target = "serviciuId", source = "serviciuInfo.id")
    @Mapping(target = "tipServiciu", source = "serviciuInfo.nume")
    @Mapping(target = "pret", source = "serviciuInfo.pret")
    @Mapping(target = "durataMinute", source = "serviciuInfo.durataMinute")
    @Mapping(target = "status", constant = "PROGRAMATA")
    @Mapping(target = "areEvaluare", constant = "false")
    @Mapping(target = "areJurnal", constant = "false")
    Programare toEntity(CreeazaProgramareRequest request,
            DetaliiServiciuDTO serviciuInfo,
            LocalTime oraSfarsitCalculata,
            boolean isPrimaIntalnire);

    @Mapping(target = "id", source = "programare.id")
    @Mapping(target = "title", source = "numePacient")
    @Mapping(target = "numeLocatie", source = "numeLocatie")
    @Mapping(target = "telefonPacient", source = "telefonPacient")
    @Mapping(target = "tipServiciu", source = "programare.tipServiciu")
    @Mapping(target = "status", source = "programare.status")
    @Mapping(target = "motivAnulare", source = "programare.motivAnulare")
    @Mapping(target = "primaIntalnire", expression = "java(Boolean.TRUE.equals(programare.getPrimaIntalnire()))")
    @Mapping(target = "start", expression = "java(java.time.LocalDateTime.of(programare.getData(), programare.getOraInceput()))")
    @Mapping(target = "end", expression = "java(java.time.LocalDateTime.of(programare.getData(), programare.getOraSfarsit()))")
    CalendarProgramareDTO toCalendarDTO(Programare programare,
                                        String numePacient,
                                        String telefonPacient,
                                        String numeLocatie);

    @Mapping(target = "id", source = "programare.id")
    @Mapping(target = "tipServiciu", source = "programare.tipServiciu")
    @Mapping(target = "data", source = "programare.data")
    @Mapping(target = "ora", source = "programare.oraInceput")
    @Mapping(target = "terapeutId", source = "programare.terapeutId")
    @Mapping(target = "terapeutKeycloakId", source = "terapeutKeycloakId")
    @Mapping(target = "numeTerapeut", source = "numeTerapeut")
    @Mapping(target = "numeLocatie", source = "numeLocatie")
    ProgramareJurnalDTO toProgramareJurnalDTO(Programare programare,
                                              String numeTerapeut,
                                              String terapeutKeycloakId,
                                              String numeLocatie);

    ProgramareResponseDTO toResponseDTO(Programare programare);
}
