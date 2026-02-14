package com.example.programari_service.mapper;

import com.example.programari_service.entity.RelatiePacientTerapeut;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class RelatieMapper {

    // creeaza o relatie noua intre pacient si terapeut
    public RelatiePacientTerapeut toEntity(Long pacientId, Long terapeutId, LocalDate dataInceput) {
        return RelatiePacientTerapeut.builder()
                .pacientId(pacientId)
                .terapeutId(terapeutId)
                .dataInceput(dataInceput)
                .activa(true)
                .build();
    }
}
