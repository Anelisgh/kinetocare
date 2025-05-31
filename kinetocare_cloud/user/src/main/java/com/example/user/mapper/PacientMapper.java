package com.example.user.mapper;

import com.example.common.dto.PacientDTO;
import com.example.common.enums.Gen;
import com.example.common.enums.TipSport;
import com.example.user.domain.Pacient;
import org.springframework.stereotype.Component;

@Component
public class PacientMapper {
    public PacientDTO toDTO(Pacient pacient) {
        return PacientDTO.builder()
                .id(pacient.getId())
                .nume(pacient.getNume())
                .telefon(pacient.getTelefon())
                .gen(Gen.valueOf(pacient.getGen().name()))
                .cnp(pacient.getCnp())
                .dataNastere(pacient.getDataNastere())
                .tipSport(TipSport.valueOf(pacient.getTipSport().name()))
                .detaliiSport(pacient.getDetaliiSport())
                .build();
    }
}
