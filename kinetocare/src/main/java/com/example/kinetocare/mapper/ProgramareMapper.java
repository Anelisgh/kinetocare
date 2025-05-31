package com.example.kinetocare.mapper;

import com.example.kinetocare.domain.Programare;
import com.example.kinetocare.dto.ProgramareDTO;
import com.example.kinetocare.dto.ServiciuDTO;
import com.example.kinetocare.repository.ServiciuRepository;
import com.example.kinetocare.repository.TerapeutRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

// Adica programarea pe care o vede pacientul; a nu se confunda cu CalendarMapper -> cum vede terapeutul programarile
@Component
@RequiredArgsConstructor
public class ProgramareMapper {
    private final TerapeutRepository terapeutRepository;
    private final ServiciuRepository serviciuRepository;

    public ProgramareDTO toDto(Programare programare) {
        return ProgramareDTO.builder()
                .id(programare.getId())
                .data(programare.getData())
                .ora(programare.getOra())
                .status(programare.getStatus())
                .numeTerapeut(programare.getTerapeut() != null ? programare.getTerapeut().getNume() : null)
                .serviciu(programare.getServiciu() != null ?
                        ServiciuDTO.builder()
                                .tipServiciu(programare.getServiciu().getTipServiciu())
                                .build()
                        : null)
                .build();
    }

    public Programare toEntity(ProgramareDTO dto) {
        return Programare.builder()
                .data(dto.getData())
                .ora(dto.getOra())
                .build();
    }
}
