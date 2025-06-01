package com.example.programare.service;

import com.example.common.dto.ProgramareDTO;
import com.example.common.dto.ProgramareDetaliiDTO;
import com.example.common.dto.ServiciuDTO;
import com.example.common.dto.TerapeutDTO;
import com.example.common.enums.Status;
import com.example.programare.domain.Programare;
import com.example.programare.feign.ServiciuFeignClient;
import com.example.programare.feign.TerapeutFeignClient;
import com.example.programare.mapper.ProgramareMapper;
import com.example.programare.repository.ProgramareRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgramareService {
    private final ProgramareRepository programareRepository;
    private final ServiciuFeignClient serviciuClient;
    private final ProgramareMapper programareMapper;
    private final TerapeutFeignClient terapeutClient;

    public ProgramareDetaliiDTO getProgramareById(Long id) {
        Programare programare = programareRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Programare negăsită"));
        return programareMapper.toDetaliiDto(programare);
    }

    @Transactional
    public ProgramareDetaliiDTO creazaProgramare(ProgramareDTO dto, String emailPacient) {
        TerapeutDTO terapeut = terapeutClient.getTerapeutByEmail(emailPacient);
        ServiciuDTO serviciu = serviciuClient.getServiciuById(dto.getServiciuId());
        if (serviciu == null) {
            throw new EntityNotFoundException("Serviciul nu există");
        }
        LocalTime oraEnd = dto.getOra().plusMinutes(serviciu.getDurataMinute());
        Programare programare = programareMapper.toEntity(dto);
        programare.setOraEnd(oraEnd);
        programare.setStatus(Status.PROGRAMATA);
        programare.setTerapeutId(terapeut.getId());
        Programare saved = programareRepository.save(programare);
        return programareMapper.toDetaliiDto(saved);
    }

    public List<ProgramareDetaliiDTO> getProgramariByPacient(Long pacientId) {
        List<Programare> programari = programareRepository.findByPacientId(pacientId);
        return programari.stream()
                .map(programareMapper::toDetaliiDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void stergeProgramare(Long id, String emailPacient) {
        Programare programare = programareRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Programare negăsită"));
        programareRepository.delete(programare);
    }

    public List<ProgramareDetaliiDTO> getCompletedSessionsAfterDate(Long pacientId, LocalDate startDate) {
        List<Programare> programari = programareRepository.findByPacientIdAndDataGreaterThanEqualAndStatus(
                pacientId, startDate, Status.FINALIZATA
        );

        return programari.stream()
                .map(programareMapper::toDetaliiDto)
                .collect(Collectors.toList());
    }
}
