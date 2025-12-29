package com.example.programari_service.repository;

import com.example.programari_service.entity.RelatiePacientTerapeut;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RelatieRepository extends JpaRepository<RelatiePacientTerapeut, Long> {
    Optional<RelatiePacientTerapeut> findByPacientIdAndTerapeutId(Long pacientId, Long terapeutId);
}