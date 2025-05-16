package com.example.kinetocare.mapper;

import com.example.kinetocare.domain.*;
import com.example.kinetocare.dto.EvaluareDTO;
import com.example.kinetocare.repository.PacientRepository;
import com.example.kinetocare.repository.ServiciuRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EvaluareMapper {
    private final ServiciuRepository serviciuRepository;
    private final PacientRepository pacientRepository;

    public Diagnostic toDiagnostic(EvaluareDTO dto, Terapeut terapeut) {
        Pacient pacient = pacientRepository.findById(dto.getPacientId())
                .orElseThrow(() -> new EntityNotFoundException("Pacient nu există"));

        Serviciu serviciu = serviciuRepository.findByTipServiciu(dto.getTipServiciu())
                .orElseThrow(() -> new EntityNotFoundException("Serviciu invalid"));

        return Diagnostic.builder()
                .nume(dto.getNumeDiagnostic())
                .data(dto.getDataEvaluare())
                .sedinteRecomandate(dto.getSedinteRecomandate())
                .pacient(pacient)
                .terapeut(terapeut)
                .serviciu(serviciu)
                .build();
    }

    public Evaluare toEvaluare(EvaluareDTO dto, Diagnostic diagnostic) {
        return Evaluare.builder()
                .tipEvaluare(dto.getTipEvaluare())
                .data(dto.getDataEvaluare())
                .observatii(dto.getObservatii())
                .diagnostic(diagnostic)
                .pacient(diagnostic.getPacient())
                .terapeut(diagnostic.getTerapeut())
                .build();
    }
}
