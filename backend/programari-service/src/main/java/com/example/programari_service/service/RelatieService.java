package com.example.programari_service.service;

import com.example.programari_service.entity.RelatiePacientTerapeut;
import com.example.programari_service.repository.RelatieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RelatieService {

    private final RelatieRepository relatieRepository;

    @Transactional
    public void asiguraRelatieActiva(Long pacientId, Long terapeutId, LocalDate dataInceput) {
        Optional<RelatiePacientTerapeut> relatieOpt = relatieRepository.findByPacientIdAndTerapeutId(pacientId, terapeutId);

        if (relatieOpt.isPresent()) {
            // Cazul 1: Relația există deja
            RelatiePacientTerapeut relatie = relatieOpt.get();
            // Dacă era inactivă (poate au terminat în trecut), o reactivăm
            if (Boolean.FALSE.equals(relatie.getActiva())) {
                relatie.setActiva(true);
                relatie.setDataSfarsit(null); // Ștergem data de sfârșit anterioară
                relatieRepository.save(relatie);
            }
            // Dacă e deja activă, nu facem nimic
        } else {
            // Cazul 2: Relație nouă
            RelatiePacientTerapeut nouaRelatie = RelatiePacientTerapeut.builder()
                    .pacientId(pacientId)
                    .terapeutId(terapeutId)
                    .dataInceput(dataInceput)
                    .activa(true)
                    .build();
            relatieRepository.save(nouaRelatie);
        }
    }
}