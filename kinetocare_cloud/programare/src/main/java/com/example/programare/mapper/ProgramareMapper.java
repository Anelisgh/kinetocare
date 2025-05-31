package com.example.programare.mapper;

import com.example.common.dto.*;
import com.example.programare.domain.Programare;
import com.example.programare.feign.PacientFeignClient;
import com.example.programare.feign.ServiciuFeignClient;
import com.example.programare.feign.TerapeutFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProgramareMapper {
    private final PacientFeignClient pacientClient;
    private final TerapeutFeignClient terapeutClient;
    private final ServiciuFeignClient serviciuClient;

    public ProgramareDetaliiDTO toDetaliiDto(Programare programare) {
        PacientDTO pacient = pacientClient.getPacientById(programare.getPacientId());
        TerapeutDTO terapeut = terapeutClient.getTerapeutById(programare.getTerapeutId());
        ServiciuDTO serviciu = serviciuClient.getServiciuById(programare.getServiciuId());

        return ProgramareDetaliiDTO.builder()
                .id(programare.getId())
                .data(programare.getData())
                .ora(programare.getOra())
                .oraEnd(programare.getOraEnd())
                .status(programare.getStatus())
                .numePacient(pacient.getNume())
                .numeTerapeut(terapeut.getNume())
                .tipServiciu(serviciu.getTipServiciu())
                .pacientId(programare.getPacientId())
                .serviciuId(programare.getServiciuId())
                .build();
    }

    public Programare toEntity(ProgramareDTO dto) {
        return Programare.builder()
                .data(dto.getData())
                .ora(dto.getOra())
                .pacientId(dto.getPacientId())
                .serviciuId(dto.getServiciuId())
                .build();
    }
}
