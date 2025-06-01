package com.example.evaluare.service;

import com.example.common.dto.EvaluareDTO;
import com.example.common.dto.PacientDTO;
import com.example.common.dto.ProgramareDetaliiDTO;
import com.example.common.dto.TerapeutDTO;
import com.example.common.enums.TipServiciu;
import com.example.evaluare.domain.Diagnostic;
import com.example.evaluare.domain.Evaluare;
import com.example.evaluare.domain.Serviciu;
import com.example.evaluare.feign.PacientFeignClient;
import com.example.evaluare.feign.ProgramareFeignClient;
import com.example.evaluare.feign.TerapeutFeignClient;
import com.example.evaluare.mapper.EvaluareMapper;
import com.example.evaluare.repository.DiagnosticRepository;
import com.example.evaluare.repository.EvaluareRepository;
import com.example.evaluare.repository.ServiciuRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EvaluareService {

    private final EvaluareRepository evaluareRepository;
    private final DiagnosticRepository diagnosticRepository;
    private final TerapeutFeignClient terapeutClient;
    private final PacientFeignClient pacientClient;
    private final ProgramareFeignClient programareClient;
    private final EvaluareMapper evaluareMapper;
    private final ServiciuRepository serviciuRepository;

    @Transactional
    public void adaugaEvaluare(EvaluareDTO evaluareDTO, String emailTerapeut) {
        TerapeutDTO terapeutDTO = terapeutClient.getTerapeutByEmail(emailTerapeut);
        if (terapeutDTO == null) {
            throw new EntityNotFoundException("Terapeutul nu există");
        }
        PacientDTO pacientDTO = pacientClient.getPacientById(evaluareDTO.getPacientId());
        if (pacientDTO == null) {
            throw new EntityNotFoundException("Pacientul nu există");
        }

        Serviciu serviciu = serviciuRepository.findByTipServiciu(evaluareDTO.getTipServiciu())
                .orElseThrow(() -> new EntityNotFoundException("Serviciu invalid"));

        Diagnostic diagnostic = evaluareMapper.toDiagnostic(
                evaluareDTO,
                terapeutDTO.getId(),
                pacientDTO.getId()
        );
        diagnosticRepository.save(diagnostic);

        Evaluare evaluare = evaluareMapper.toEvaluare(
                evaluareDTO,
                diagnostic.getId(),
                terapeutDTO.getId(),
                pacientDTO.getId()
        );
        evaluareRepository.save(evaluare);
    }

    public List<TipServiciu> getServiciiFiltrate() {
        return Arrays.stream(TipServiciu.values())
                .filter(t -> t != TipServiciu.EVALUARE && t != TipServiciu.REEVALUARE)
                .collect(Collectors.toList());
    }

    public String extractEmailFromToken(String token) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    private void setSedintePanaLaReevaluare(EvaluareDTO dto, Evaluare evaluare) {
        try {
            List<ProgramareDetaliiDTO> programari = programareClient.getCompletedSessionsAfterDate(
                    evaluare.getPacientId(),
                    evaluare.getData()
            );
            long sedinteEfectuate = programari.stream()
                    .filter(p -> p.getTipServiciu() != TipServiciu.EVALUARE)
                    .filter(p -> p.getTipServiciu() != TipServiciu.REEVALUARE)
                    .count();
            int sedinteRamase = Math.max(dto.getSedinteRecomandate() - (int) sedinteEfectuate, 0);
            dto.setSedintePanaLaReevaluare(sedinteRamase);
        } catch (Exception e) {
            dto.setSedintePanaLaReevaluare(null);
        }
    }

    public List<EvaluareDTO> getEvaluariByTerapeutId(Long terapeutId) {
        List<Evaluare> evaluari = evaluareRepository.findByTerapeutId(terapeutId);
        return evaluari.stream()
                .map(evaluare -> {
                    EvaluareDTO dto = evaluareMapper.toDto(evaluare);
                    setSedintePanaLaReevaluare(dto, evaluare);
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
