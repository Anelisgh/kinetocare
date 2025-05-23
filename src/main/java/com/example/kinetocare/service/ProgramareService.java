package com.example.kinetocare.service;

import com.example.kinetocare.domain.*;
import com.example.kinetocare.dto.ProgramareDTO;
import com.example.kinetocare.exception.ConflictException;
import com.example.kinetocare.mapper.ProgramareMapper;
import com.example.kinetocare.repository.ProgramareRepository;
import com.example.kinetocare.repository.ServiciuRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class ProgramareService {
    private final ProgramareRepository programareRepository;
    private final ServiciuRepository serviciuRepository;
    private final ProgramareMapper programareMapper;
    private final PacientService pacientService;

// PACIENT CREEAZA PROGRAMARE
// ‧˚₊꒷꒦︶︶︶︶︶꒷꒦︶︶︶︶︶꒦꒷‧₊˚⊹
    @Transactional
    public ProgramareDTO creazaProgramare(@Valid ProgramareDTO programareDTO, String emailPacient) {
        log.info("Creating appointment for {}", emailPacient);
        Pacient pacient = pacientService.getPacientByEmail(emailPacient);

        // ultima evaluare
        Evaluare ultimaEvaluare = pacient.getEvaluari().stream()
                .max(Comparator.comparing(Evaluare::getData))
                .orElseThrow(() -> {
                    log.error("No evaluation found for patient {}", emailPacient);
                    return new RuntimeException("Pacientul nu are evaluare");
                });

        // sedintele efectuate de la ultima evaluare
        long sedinteEfectuate = pacient.getProgramari().stream()
                .filter(p -> p.getStatus() == Status.FINALIZATA)
                .filter(p -> p.getData().isAfter(ultimaEvaluare.getData()))
                .filter(p -> {
                    if (p.getServiciu() == null || p.getServiciu().getTipServiciu() == null) {
                        return false;
                    }
                    return !p.getServiciu().getTipServiciu().name().matches("EVALUARE|REEVALUARE");
                })
                .count();

        Serviciu serviciu;
        if (ultimaEvaluare.getDiagnostic().getSedinteRecomandate() - sedinteEfectuate <= 0) {
            serviciu = serviciuRepository.findByTipServiciu(TipServiciu.REEVALUARE)
                    .orElseThrow(() -> new RuntimeException("Serviciu de reevaluare nedefinit"));
        } else {
            serviciu = ultimaEvaluare.getDiagnostic().getServiciu();
        }

        Programare programare = new Programare();
        programare.setData(programareDTO.getData());
        programare.setOra(programareDTO.getOra());
        programare.setPacient(pacient);
        programare.setTerapeut(pacient.getTerapeut());
        programare.setServiciu(serviciu);
        programare.setStatus(Status.PROGRAMATA);

        if (serviciu != null && programare.getOra() != null) {
            programare.setOraEnd(programare.getOra().plusMinutes(serviciu.getDurataMinute()));
        }

        log.debug("Checking for overlapping appointments...");
        List<Programare> suprapuneri = programareRepository.findProgramariSuprapuse(
                programare.getTerapeut(),
                programare.getData(),
                programare.getOra(),
                programare.getOraEnd()
        );

        if (!suprapuneri.isEmpty()) {
            log.warn("Appointment conflict detected for therapist {}", programare.getTerapeut().getId());
            throw new ConflictException("Terapeutul are deja o programare în acest interval");
        }

        Programare saved = programareRepository.save(programare);
        log.info("Appointment {} created successfully", saved.getId());
        return programareMapper.toDto(saved);
    }

// PACIENT MODIFICA PROGRAMARE
//‧˚₊꒷꒦︶︶︶︶︶꒷꒦︶︶︶︶︶꒦꒷‧₊˚⊹
    @Transactional
    public ProgramareDTO modificaProgramare(@Valid ProgramareDTO programareDTO, String emailPacient) {
        log.info("Modifying appointment {}", programareDTO.getId());
        Programare programare = programareRepository.findById(programareDTO.getId())
                .orElseThrow(() -> {
                    log.error("Appointment {} not found", programareDTO.getId());
                    return new EntityNotFoundException("Programare nu există");
                });

        if (!programare.getPacient().getUser().getEmail().equals(emailPacient)) {
            log.warn("Unauthorized modification attempt by {}", emailPacient);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Nu aveți dreptul să modificați această programare");
        }

        programare.setData(programareDTO.getData());
        programare.setOra(programareDTO.getOra());

        if (programare.getServiciu() != null && programare.getOra() != null) {
            programare.setOraEnd(programare.getOra().plusMinutes(programare.getServiciu().getDurataMinute()));
        }

        List<Programare> suprapuneri = programareRepository.findProgramariSuprapuseExcluzandCurrent(
                programare.getId(),
                programare.getTerapeut(),
                programare.getData(),
                programare.getOra(),
                programare.getOraEnd()
        );

        if (!suprapuneri.isEmpty()) {
            throw new ConflictException("Terapeutul are deja o programare în acest interval");
        }

        Programare updated = programareRepository.save(programare);
        return programareMapper.toDto(updated);
    }

// PACIENT STERGE PROGRAMARE
//‧˚₊꒷꒦︶︶︶︶︶꒷꒦︶︶︶︶︶꒦꒷‧₊˚⊹
    @Transactional
    public void stergeProgramare(Long programareId, String emailPacient) {
        log.info("Deleting appointment {}", programareId);
        Programare programare = programareRepository.findById(programareId)
                .orElseThrow(() -> {
                    log.error("Appointment {} not found for deletion", programareId);
                    return new EntityNotFoundException("Programarea nu există");
                });

        if (!programare.getPacient().getUser().getEmail().equals(emailPacient)) {
            log.warn("Unauthorized deletion attempt by {}", emailPacient);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Nu aveți dreptul să modificați această programare");
        }

        programareRepository.delete(programare);
        log.debug("Appointment {} deleted", programareId);
    }

    public Programare getProgramareById(Long id) {
        return programareRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Programarea nu există"));
    }
}

