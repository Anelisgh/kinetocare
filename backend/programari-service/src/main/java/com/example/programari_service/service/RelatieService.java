package com.example.programari_service.service;

import com.example.programari_service.entity.RelatiePacientTerapeut;
import com.example.programari_service.mapper.RelatieMapper;
import com.example.programari_service.repository.RelatieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RelatieService {

    private final RelatieRepository relatieRepository;
    private final RelatieMapper relatieMapper;

    // asigura ca relatia e activa
    @Transactional
    public void asiguraRelatieActiva(Long pacientId, Long terapeutId, LocalDate dataInceput) {
        Optional<RelatiePacientTerapeut> relatieOpt = relatieRepository.findByPacientIdAndTerapeutId(pacientId, terapeutId);

        if (relatieOpt.isPresent()) {
            // cazul 1: relatia exista deja
            RelatiePacientTerapeut relatie = relatieOpt.get();
            // daca era inactiva (poate au lucrat in trecut), o reactivam
            if (Boolean.FALSE.equals(relatie.getActiva())) {
                relatie.setActiva(true);
                relatie.setDataSfarsit(null); // stergem data de sfarsit anterioara
                relatieRepository.save(relatie);
            }
            // daca e deja activa, nu facem nimic
        } else {
            // cazul 2: relatia noua
            RelatiePacientTerapeut nouaRelatie = relatieMapper.toEntity(pacientId, terapeutId, dataInceput);
            relatieRepository.save(nouaRelatie);
        }
    }
}