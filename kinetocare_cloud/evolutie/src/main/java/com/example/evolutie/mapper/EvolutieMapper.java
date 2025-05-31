package com.example.evolutie.mapper;

import com.example.common.dto.EvolutieDTO;
import com.example.common.dto.PacientDTO;
import com.example.common.dto.TerapeutDTO;
import com.example.evolutie.domain.Evolutie;
import com.example.evolutie.feign.PacientFeignClient;
import com.example.evolutie.feign.TerapeutFeignClient;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EvolutieMapper {
    private final PacientFeignClient pacientClient;
    private final TerapeutFeignClient terapeutClient;

    public Evolutie toEvolutie(EvolutieDTO dto, String emailTerapeut) {
        TerapeutDTO terapeut = terapeutClient.getTerapeutByEmail(emailTerapeut);
        if (terapeut == null) {
            throw new EntityNotFoundException("Terapeutul nu există");
        }
        PacientDTO pacient = pacientClient.getPacientById(dto.getPacientId());
        if (pacient == null) {
            throw new EntityNotFoundException("Pacientul nu există");
        }

        return Evolutie.builder()
                .data(dto.getDataEvolutie())
                .observatii(dto.getObservatii())
                .pacientId(pacient.getId())
                .terapeutId(terapeut.getId())
                .build();
    }

    public EvolutieDTO toDto(Evolutie evolutie) {
        return EvolutieDTO.builder()
                .pacientId(evolutie.getPacientId())
                .dataEvolutie(evolutie.getData())
                .observatii(evolutie.getObservatii())
                .build();
    }
}
