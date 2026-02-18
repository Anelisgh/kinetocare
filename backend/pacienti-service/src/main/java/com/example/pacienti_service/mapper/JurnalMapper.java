package com.example.pacienti_service.mapper;

import com.example.pacienti_service.dto.JurnalIstoricDTO;
import com.example.pacienti_service.dto.ProgramareJurnalDTO;
import com.example.pacienti_service.entity.JurnalPacient;
import org.springframework.stereotype.Component;

@Component
public class JurnalMapper {

    public JurnalIstoricDTO toIstoricDTO(JurnalPacient jurnal, ProgramareJurnalDTO detalii) {
        return JurnalIstoricDTO.builder()
                .id(jurnal.getId())
                .programareId(jurnal.getProgramareId())
                .dataJurnal(jurnal.getData())
                .oraSedinta(detalii != null ? detalii.getOra() : null)
                .nivelDurere(jurnal.getNivelDurere())
                .dificultateExercitii(jurnal.getDificultateExercitii())
                .nivelOboseala(jurnal.getNivelOboseala())
                .comentarii(jurnal.getComentarii())
                // Detalii îmbogățite din programare
                .tipServiciu(detalii != null ? detalii.getTipServiciu() : null)
                .numeTerapeut(detalii != null ? detalii.getNumeTerapeut() : null)
                .numeLocatie(detalii != null ? detalii.getNumeLocatie() : null)
                .build();
    }
}
