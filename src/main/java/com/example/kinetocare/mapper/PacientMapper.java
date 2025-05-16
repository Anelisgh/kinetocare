package com.example.kinetocare.mapper;

import com.example.kinetocare.domain.*;
import com.example.kinetocare.dto.EvaluareDTO;
import com.example.kinetocare.dto.EvolutieDTO;
import com.example.kinetocare.dto.PacientDetaliiDTO;
import com.example.kinetocare.dto.PacientDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class PacientMapper {

    public PacientDTO toPacientDTO(Pacient pacient) {
        String varsta = calculeazaVarsta(pacient.getDataNastere()) + " ani";
        String diagnostic = pacient.getDiagnostice() != null && !pacient.getDiagnostice().isEmpty()
                ? pacient.getDiagnostice().get(0).getNume()
                : null;

        return new PacientDTO(
                pacient.getId(),
                pacient.getNume(),
                varsta,
                diagnostic
        );
    }

    public PacientDetaliiDTO toPacientDetaliiDTO(Pacient pacient, List<Evaluare> evaluari, List<Evolutie> evolutii) {
        List<EvaluareDTO> evaluareDTOs = evaluari.stream()
                .map(this::toEvaluareDTO)
                .collect(Collectors.toList());

        List<EvolutieDTO> evolutieDTOs = evolutii.stream()
                .map(this::toEvolutieDTO)
                .collect(Collectors.toList());

        return new PacientDetaliiDTO(
                pacient.getId(),
                pacient.getNume(),
                pacient.getGen() != null ? pacient.getGen().getDisplayName() : null,
                pacient.getTelefon(),
                pacient.getEmail(),
                calculeazaVarsta(pacient.getDataNastere()),
                evaluareDTOs,
                evolutieDTOs
        );
    }

    private EvaluareDTO toEvaluareDTO(Evaluare evaluare) {
        Integer sedinteRecomandate = Optional.ofNullable(evaluare.getDiagnostic())
                .map(Diagnostic::getSedinteRecomandate)
                .orElse(0);

        long sedinteEfectuate = Optional.ofNullable(evaluare.getPacient())
                .map(p -> p.getProgramari().stream()
                        .filter(pr -> pr.getStatus() == Status.FINALIZATA)
                        .filter(pr -> !pr.getData().isBefore(evaluare.getData()))
                        .filter(pr -> {
                            if (pr.getServiciu() == null || pr.getServiciu().getTipServiciu() == null) {
                                return false;
                            }
                            String tipServiciu = pr.getServiciu().getTipServiciu().name();
                            return !tipServiciu.equals("EVALUARE") && !tipServiciu.equals("REEVALUARE");
                        })
                        .count())
                .orElse(0L);

        int sedinteRamase = Math.max(sedinteRecomandate - (int) sedinteEfectuate, 0);

        return EvaluareDTO.builder()
                .pacientId(evaluare.getPacient() != null ? evaluare.getPacient().getId() : null)
                .tipEvaluare(evaluare.getTipEvaluare())
                .dataEvaluare(evaluare.getData())
                .numeDiagnostic(Optional.ofNullable(evaluare.getDiagnostic())
                        .map(Diagnostic::getNume)
                        .orElse(null))
                .sedinteRecomandate(sedinteRecomandate)
                .sedintePanaLaReevaluare(sedinteRamase)
                .tipServiciu(Optional.ofNullable(evaluare.getDiagnostic())
                        .map(Diagnostic::getServiciu)
                        .map(Serviciu::getTipServiciu)
                        .orElse(null))
                .observatii(evaluare.getObservatii())
                .build();
    }

    private EvolutieDTO toEvolutieDTO(Evolutie evolutie) {
        return new EvolutieDTO(
                evolutie.getPacient().getId(),
                evolutie.getData(),
                evolutie.getObservatii()
        );
    }

    private int calculeazaVarsta(LocalDate dataNastere) {
        if (dataNastere == null) return 0;
        return Period.between(dataNastere, LocalDate.now()).getYears();
    }
}
