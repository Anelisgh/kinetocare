package com.example.kinetocare.service;


import com.example.kinetocare.domain.Programare;
import com.example.kinetocare.domain.Serviciu;
import com.example.kinetocare.domain.Status;
import com.example.kinetocare.domain.Terapeut;
import com.example.kinetocare.dto.ProgramareTerapeutDTO;
import com.example.kinetocare.mapper.CalendarMapper;
import com.example.kinetocare.repository.ProgramareRepository;
import com.example.kinetocare.repository.TerapeutRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalendarService {
    private final ProgramareRepository programareRepository;
    private final CalendarMapper calendarMapper;
    private final TerapeutRepository terapeutRepository;
    private final ProgramareService programareService;
    private final PlataService plataService;

// EXTRAGE PROGRAMĂRILE
//‧˚₊꒷꒦︶︶︶︶︶꒷꒦︶︶︶︶︶꒦꒷‧₊˚⊹
    public List<ProgramareTerapeutDTO> getProgramariForTerapeut(String email) {
        Terapeut terapeut = terapeutRepository.findByUserEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Terapeutul nu există"));
        return programareRepository.findByTerapeut(terapeut).stream()
                .map(calendarMapper::toProgramareTerapeutDTO)
                .collect(Collectors.toList());
    }
// EXTRAGE PROGRAMĂRILE PENTRU CALENDAR, între 2 date
//‧˚₊꒷꒦︶︶︶︶︶꒷꒦︶︶︶︶︶꒦꒷‧₊˚⊹
    public List<ProgramareTerapeutDTO> getProgramariForCalendar(String email, LocalDate start, LocalDate end) {
        Terapeut terapeut = terapeutRepository.findByUserEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Terapeutul nu există"));
        return programareRepository.findByTerapeutAndDataBetween(terapeut, start, end).stream()
                .map(calendarMapper::toProgramareTerapeutDTO)
                .collect(Collectors.toList());
    }
// ACTUALIZARE PROGRAMARE
//‧˚₊꒷꒦︶︶︶︶︶꒷꒦︶︶︶︶︶꒦꒷‧₊˚⊹
    @Transactional
    public void updateProgramare(Long programareId, LocalDate newDate, LocalTime newTime, Status newStatus) {
        Programare programare = programareService.getProgramareById(programareId);
        Serviciu serviciu = programare.getServiciu();
        LocalTime newOraEnd = newTime.plusMinutes(serviciu.getDurataMinute());
        List<Programare> conflicte = programareRepository.findConflictingAppointments(
                programare.getTerapeut(),
                newDate,
                newTime,
                newOraEnd
        );
        conflicte.removeIf(p -> p.getId().equals(programareId));
        if (!conflicte.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Există o programare conflictuală în intervalul " +
                    newTime + " - " + newOraEnd);
        }
        programare.setData(newDate);
        programare.setOra(newTime);
        programare.setStatus(newStatus);
        if (newStatus == Status.FINALIZATA) {
            plataService.creazaPlata(programare);
        }
        programareRepository.save(programare);
    }
}