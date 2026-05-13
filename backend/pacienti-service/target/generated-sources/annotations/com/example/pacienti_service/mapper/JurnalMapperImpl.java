package com.example.pacienti_service.mapper;

import com.example.pacienti_service.dto.JurnalIstoricDTO;
import com.example.pacienti_service.dto.ProgramareJurnalDTO;
import com.example.pacienti_service.entity.JurnalPacient;
import java.time.LocalDate;
import java.time.LocalTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-13T13:22:07+0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class JurnalMapperImpl implements JurnalMapper {

    @Override
    public JurnalIstoricDTO toIstoricDTO(JurnalPacient jurnal, ProgramareJurnalDTO detalii) {
        if ( jurnal == null && detalii == null ) {
            return null;
        }

        Long id = null;
        Long programareId = null;
        LocalDate dataJurnal = null;
        Integer nivelDurere = null;
        Integer dificultateExercitii = null;
        Integer nivelOboseala = null;
        String comentarii = null;
        if ( jurnal != null ) {
            id = jurnal.getId();
            programareId = jurnal.getProgramareId();
            dataJurnal = jurnal.getData();
            nivelDurere = jurnal.getNivelDurere();
            dificultateExercitii = jurnal.getDificultateExercitii();
            nivelOboseala = jurnal.getNivelOboseala();
            comentarii = jurnal.getComentarii();
        }
        LocalTime oraSedinta = null;
        String tipServiciu = null;
        String numeTerapeut = null;
        String numeLocatie = null;
        if ( detalii != null ) {
            oraSedinta = detalii.ora();
            tipServiciu = detalii.tipServiciu();
            numeTerapeut = detalii.numeTerapeut();
            numeLocatie = detalii.numeLocatie();
        }

        JurnalIstoricDTO jurnalIstoricDTO = new JurnalIstoricDTO( id, programareId, dataJurnal, oraSedinta, nivelDurere, dificultateExercitii, nivelOboseala, comentarii, tipServiciu, numeTerapeut, numeLocatie );

        return jurnalIstoricDTO;
    }
}
