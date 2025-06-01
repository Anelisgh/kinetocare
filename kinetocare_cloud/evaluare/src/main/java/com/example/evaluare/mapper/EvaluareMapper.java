package com.example.evaluare.mapper;

import com.example.common.dto.EvaluareDTO;
import com.example.evaluare.domain.Diagnostic;
import com.example.evaluare.domain.Evaluare;
import com.example.evaluare.domain.Serviciu;
import com.example.evaluare.repository.DiagnosticRepository;
import com.example.evaluare.repository.ServiciuRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EvaluareMapper {
    private final DiagnosticRepository diagnosticRepository;
    private final ServiciuRepository serviciuRepository;

    public EvaluareDTO toDto(Evaluare evaluare) {
        Diagnostic diagnostic = diagnosticRepository.findById(evaluare.getDiagnosticId())
                .orElseThrow(() -> new EntityNotFoundException("Diagnosticul nu există"));

        Serviciu serviciu = serviciuRepository.findById(diagnostic.getServiciuId())
                .orElseThrow(() -> new EntityNotFoundException("Serviciul nu există"));

        return EvaluareDTO.builder()
                .pacientId(evaluare.getPacientId())
                .tipEvaluare(evaluare.getTipEvaluare())
                .dataEvaluare(evaluare.getData())
                .numeDiagnostic(diagnostic.getNume())
                .sedinteRecomandate(diagnostic.getSedinteRecomandate())
                .tipServiciu(serviciu.getTipServiciu())
                .observatii(evaluare.getObservatii())
                .build();
    }

    public Diagnostic toDiagnostic(EvaluareDTO dto, Long terapeutId, Long pacientId) {
        Serviciu serviciu = serviciuRepository.findByTipServiciu(dto.getTipServiciu())
                .orElseThrow(() -> new EntityNotFoundException("Serviciu invalid"));

        return Diagnostic.builder()
                .nume(dto.getNumeDiagnostic())
                .data(dto.getDataEvaluare())
                .sedinteRecomandate(dto.getSedinteRecomandate())
                .terapeutId(terapeutId)
                .pacientId(pacientId)
                .serviciuId(serviciu.getId())
                .build();
    }

    // Converteste DTO în Evaluare
    public Evaluare toEvaluare(EvaluareDTO dto, Long diagnosticId, Long terapeutId, Long pacientId) {
        return Evaluare.builder()
                .tipEvaluare(dto.getTipEvaluare())
                .data(dto.getDataEvaluare())
                .observatii(dto.getObservatii())
                .diagnosticId(diagnosticId)
                .terapeutId(terapeutId)
                .pacientId(pacientId)
                .build();
    }
}