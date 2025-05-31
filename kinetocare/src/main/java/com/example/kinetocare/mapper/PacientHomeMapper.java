package com.example.kinetocare.mapper;

import com.example.kinetocare.domain.*;
import com.example.kinetocare.dto.PacientHomeDTO;
import com.example.kinetocare.dto.ProgramareDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.time.LocalDateTime;
import java.util.Comparator;

@Component
@RequiredArgsConstructor
public class PacientHomeMapper {

    private final ProgramareMapper programareMapper;

    public PacientHomeDTO mapToDto(Pacient pacient) {
        LocalDate azi = LocalDate.now();

        // Cea mai recenta evaluare
        Evaluare evaluare = pacient.getEvaluari() != null
                ? pacient.getEvaluari().stream()
                .max(Comparator.comparing(Evaluare::getData))
                .orElse(null)
                : null;

        String diagnostic = evaluare != null && evaluare.getDiagnostic() != null
                ? evaluare.getDiagnostic().getNume()
                : null;

        Integer sedintePanaLaReevaluare = null;
        if (evaluare != null && evaluare.getData() != null && evaluare.getDiagnostic() != null) {
            // Calculeaza sedintele efectuate
            long sedinteEfectuate = pacient.getProgramari().stream()
                    .filter(p -> p.getStatus() == Status.FINALIZATA)
                    .filter(p -> !p.getData().isBefore(evaluare.getData()))
                    .filter(p -> {
                        if (p.getServiciu() == null || p.getServiciu().getTipServiciu() == null) {
                            return false;
                        }
                        return !p.getServiciu().getTipServiciu().name().matches("EVALUARE|REEVALUARE");
                    })
                    .count();

            int totalRecomandate = evaluare.getDiagnostic().getSedinteRecomandate();
            sedintePanaLaReevaluare = (int) (totalRecomandate - sedinteEfectuate);
            sedintePanaLaReevaluare = Math.max(sedintePanaLaReevaluare, 0);
        }

        Programare urmatoareaProgramare = pacient.getProgramari().stream()
                .filter(p -> p.getData() != null && !p.getData().isBefore(azi))
                .filter(p -> p.getStatus() == Status.PROGRAMATA)
                .min(Comparator.comparing(Programare::getData)
                        .thenComparing(Programare::getOra))
                .orElse(null);

        ProgramareDTO nouaProgramare = new ProgramareDTO();
        if (urmatoareaProgramare != null) {
            nouaProgramare.setId(urmatoareaProgramare.getId());
            nouaProgramare.setData(urmatoareaProgramare.getData());
            nouaProgramare.setOra(urmatoareaProgramare.getOra());
        }

        Terapeut terapeut = pacient.getTerapeut();

        return PacientHomeDTO.builder()
                .nume(pacient.getNume())
                .varsta(calculeazaVarsta(pacient.getDataNastere()))
                .diagnostic(diagnostic)
                .sedintePanaLaReevaluare(sedintePanaLaReevaluare)
                .urmatoareaProgramare(urmatoareaProgramare != null ?
                        programareMapper.toDto(urmatoareaProgramare) : null)
                .nouaProgramare(nouaProgramare)
                .numarTerapeut(terapeut != null ? terapeut.getTelefon() : null)
                .build();
    }

    private int calculeazaVarsta(LocalDate dataNastere) {
        if (dataNastere == null) return 0;
        return Period.between(dataNastere, LocalDate.now()).getYears();
    }
}