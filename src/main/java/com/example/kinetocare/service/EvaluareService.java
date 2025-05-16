package com.example.kinetocare.service;

import com.example.kinetocare.domain.Diagnostic;
import com.example.kinetocare.domain.Evaluare;
import com.example.kinetocare.domain.Pacient;
import com.example.kinetocare.domain.Terapeut;
import com.example.kinetocare.dto.EvaluareDTO;
import com.example.kinetocare.mapper.EvaluareMapper;
import com.example.kinetocare.repository.DiagnosticRepository;
import com.example.kinetocare.repository.EvaluareRepository;
import com.example.kinetocare.repository.PacientRepository;
import com.example.kinetocare.repository.TerapeutRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;


@Service
@Validated
@RequiredArgsConstructor
public class EvaluareService {
    private final EvaluareMapper evaluareMapper;
    private final DiagnosticRepository diagnosticRepository;
    private final EvaluareRepository evaluareRepository;
    private final TerapeutRepository terapeutRepository;
    private final PacientRepository pacientRepository;

    @Transactional
    public void adaugaEvaluare(@Valid EvaluareDTO evaluareDTO, String emailTerapeut) {
        Terapeut terapeut = terapeutRepository.findByUserEmail(emailTerapeut)
                .orElseThrow(() -> new EntityNotFoundException("Terapeut nu există"));

        Diagnostic diagnostic = evaluareMapper.toDiagnostic(evaluareDTO, terapeut);

        Pacient pacient = diagnostic.getPacient();
        pacient.setTerapeut(terapeut);
        pacientRepository.save(pacient);

        diagnosticRepository.save(diagnostic);
        Evaluare evaluare = evaluareMapper.toEvaluare(evaluareDTO, diagnostic);
        evaluare.setPacient(pacient);
        evaluare.setTerapeut(terapeut);
        evaluareRepository.save(evaluare);
    }
}