package com.example.kinetocare.mapper;

import com.example.kinetocare.domain.Programare;
import com.example.kinetocare.domain.Serviciu;
import com.example.kinetocare.dto.ProgramareTerapeutDTO;
import com.example.kinetocare.repository.ServiciuRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class CalendarMapper {
    private final ServiciuRepository serviciuRepository;

    public ProgramareTerapeutDTO toProgramareTerapeutDTO(Programare programare) {
        Serviciu serviciu = serviciuRepository.findById(programare.getServiciu().getId())
                .orElseThrow(() -> new EntityNotFoundException("Serviciul nu există"));

        LocalTime oraEnd = programare.getOra().plusMinutes(serviciu.getDurataMinute());

        return new ProgramareTerapeutDTO(
                programare.getId(),
                programare.getPacient().getId(),
                programare.getPacient().getNume(),
                programare.getData(),
                programare.getOra(),
                programare.getOraEnd(),
                serviciu.getTipServiciu(),
                programare.getStatus()
        );
    }
}
