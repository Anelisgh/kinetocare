package com.example.kinetocare.service;

import com.example.kinetocare.domain.Evaluare;
import com.example.kinetocare.domain.Evolutie;
import com.example.kinetocare.domain.Pacient;
import com.example.kinetocare.domain.Terapeut;
import com.example.kinetocare.dto.PacientDTO;
import com.example.kinetocare.dto.PacientDetaliiDTO;
import com.example.kinetocare.dto.PacientHomeDTO;
import com.example.kinetocare.mapper.PacientHomeMapper;
import com.example.kinetocare.mapper.PacientMapper;
import com.example.kinetocare.repository.EvaluareRepository;
import com.example.kinetocare.repository.EvolutieRepository;
import com.example.kinetocare.repository.PacientRepository;
import com.example.kinetocare.repository.TerapeutRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PacientService {
    private final PacientRepository pacientRepository;
    private final EvaluareRepository evaluareRepository;
    private final EvolutieRepository evolutieRepository;
    private final TerapeutRepository terapeutRepository;
    private final PacientMapper pacientMapper;
    private final PacientHomeMapper pacientHomeMapper;

    // Datele pe care le vede terapeutul despre pacienti
    public List<PacientDTO> getPacientiPentruTerapeut(String email) {
        log.info("Fetching patients for therapist {}", email);
        Terapeut terapeut = terapeutRepository.findByUserEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Terapeutul nu există"));
        List<Pacient> pacienti = pacientRepository.findByTerapeutOrderByNumeAsc(terapeut);
        return pacienti.stream()
                .map(pacientMapper::toPacientDTO)
                .toList();
    }

    public PacientDetaliiDTO getDetaliiPacient(Long pacientId) {
        log.debug("Fetching details for patient {}", pacientId);
        Pacient pacient = pacientRepository.findById(pacientId)
                .orElseThrow(() -> {
                    log.error("Patient {} not found", pacientId);
                    return new EntityNotFoundException("Pacientul nu există");
                });

        List<Evaluare> evaluari = evaluareRepository.findByDiagnosticPacientOrderByDataDesc(pacient);
        List<Evolutie> evolutii = evolutieRepository.findByPacientOrderByDataDesc(pacient);

        return pacientMapper.toPacientDetaliiDTO(pacient, evaluari, evolutii);
    }

    // Datele pe care le vede pacientul
    public PacientHomeDTO getPacientHomeDTO(String email) {
        log.info("Building homepage for {}", email);
        Pacient pacient = pacientRepository.findByUserEmail(email)
                .orElseThrow(() -> {
                    log.error("Patient with email {} not found", email);
                    return new RuntimeException("Pacientul nu a fost găsit");
                });
        return pacientHomeMapper.mapToDto(pacient);
    }

    public Pacient getPacientByEmail(String email) {
        return pacientRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Pacient nu există"));
    }

    public String getFormAction(PacientHomeDTO dto) {
        return dto.getUrmatoareaProgramare() != null
                ? "/pacient/programari/modifica"
                : "/pacient/programari/creaza";
    }

    public boolean hasProgramare(PacientHomeDTO dto) {
        return dto.getUrmatoareaProgramare() != null;
    }
}
