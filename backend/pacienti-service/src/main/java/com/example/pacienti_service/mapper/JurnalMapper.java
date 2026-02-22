package com.example.pacienti_service.mapper;

import com.example.pacienti_service.dto.JurnalIstoricDTO;
import com.example.pacienti_service.dto.ProgramareJurnalDTO;
import com.example.pacienti_service.entity.JurnalPacient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface JurnalMapper {

    @Mapping(target = "id", source = "jurnal.id")
    @Mapping(target = "programareId", source = "jurnal.programareId")
    @Mapping(target = "dataJurnal", source = "jurnal.data")
    @Mapping(target = "oraSedinta", source = "detalii.ora")
    @Mapping(target = "nivelDurere", source = "jurnal.nivelDurere")
    @Mapping(target = "dificultateExercitii", source = "jurnal.dificultateExercitii")
    @Mapping(target = "nivelOboseala", source = "jurnal.nivelOboseala")
    @Mapping(target = "comentarii", source = "jurnal.comentarii")
    @Mapping(target = "tipServiciu", source = "detalii.tipServiciu")
    @Mapping(target = "numeTerapeut", source = "detalii.numeTerapeut")
    @Mapping(target = "numeLocatie", source = "detalii.numeLocatie")
    JurnalIstoricDTO toIstoricDTO(JurnalPacient jurnal, ProgramareJurnalDTO detalii);
}
