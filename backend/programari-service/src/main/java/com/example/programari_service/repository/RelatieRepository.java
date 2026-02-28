package com.example.programari_service.repository;

import com.example.programari_service.entity.RelatiePacientTerapeut;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RelatieRepository extends JpaRepository<RelatiePacientTerapeut, Long> {
    Optional<RelatiePacientTerapeut> findByPacientKeycloakIdAndTerapeutKeycloakId(String pacientKeycloakId, String terapeutKeycloakId);

    // pacientii activi ai unui terapeut
    List<RelatiePacientTerapeut> findByTerapeutKeycloakIdAndActivaTrue(String terapeutKeycloakId);

    // pacientii anteriori (arhiva) ai unui terapeut
    List<RelatiePacientTerapeut> findByTerapeutKeycloakIdAndActivaFalse(String terapeutKeycloakId);

    // relatia activa a unui pacient (cu orice terapeut)
    Optional<RelatiePacientTerapeut> findByPacientKeycloakIdAndActivaTrue(String pacientKeycloakId);
}