package com.example.kinetocare.mapper;

import com.example.kinetocare.domain.Evolutie;
import com.example.kinetocare.domain.Pacient;
import com.example.kinetocare.domain.Terapeut;
import com.example.kinetocare.dto.EvolutieDTO;
import com.example.kinetocare.repository.PacientRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EvolutieMapper {
    private final PacientRepository pacientRepository;

    public Evolutie toEvolutie(EvolutieDTO dto, Terapeut terapeut) {
        Pacient pacient = pacientRepository.findById(dto.getPacientId())
                .orElseThrow(() -> new EntityNotFoundException("Pacient nu există"));

        return Evolutie.builder()
                .data(dto.getDataEvolutie())
                .observatii(dto.getObservatii())
                .pacient(pacient)
                .terapeut(terapeut)
                .build();
    }

    public EvolutieDTO toEvolutieDTO(Evolutie evolutie) {
        return EvolutieDTO.builder()
                .pacientId(evolutie.getPacient().getId())
                .dataEvolutie(evolutie.getData())
                .observatii(evolutie.getObservatii())
                .build();
    }
}

