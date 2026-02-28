package com.example.programari_service.mapper;

import com.example.programari_service.dto.IstoricProgramareDTO;
import com.example.programari_service.dto.DetaliiJurnalDTO;
import com.example.programari_service.entity.Evaluare;
import com.example.programari_service.entity.Programare;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IstoricProgramareMapper {

    @Mapping(target = "id", source = "p.id")
    @Mapping(target = "data", source = "p.data")
    @Mapping(target = "oraInceput", source = "p.oraInceput")
    @Mapping(target = "oraSfarsit", source = "p.oraSfarsit")
    @Mapping(target = "tipServiciu", source = "p.tipServiciu")
    @Mapping(target = "pret", source = "p.pret")
    @Mapping(target = "status", source = "p.status")
    @Mapping(target = "terapeutKeycloakId", source = "p.terapeutKeycloakId")
    @Mapping(target = "numeTerapeut", source = "numeTerapeut")
    @Mapping(target = "numeLocatie", source = "numeLocatie")
    @Mapping(target = "areEvaluare", source = "p.areEvaluare")
    @Mapping(target = "areJurnal", source = "p.areJurnal")
    @Mapping(target = "motivAnulare", expression = "java(p.getMotivAnulare() != null ? p.getMotivAnulare().getDisplayName() : null)")
    @Mapping(target = "detaliiJurnal", source = "detaliiJurnal")
    @Mapping(target = "diagnostic", source = "evaluare.diagnostic")
    @Mapping(target = "sedinteRecomandate", source = "evaluare.sedinteRecomandate")
    @Mapping(target = "observatii", source = "evaluare.observatii")
    @Mapping(target = "serviciuRecomandat", source = "serviciuRecomandat")
    IstoricProgramareDTO toDTO(Programare p, String numeTerapeut, String numeLocatie, Evaluare evaluare, String serviciuRecomandat, DetaliiJurnalDTO detaliiJurnal);
}
