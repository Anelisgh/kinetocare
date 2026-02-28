package com.example.programari_service.repository;

import com.example.programari_service.entity.Evolutie;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EvolutieRepository extends JpaRepository<Evolutie, Long> {
    // istoricul evolutiilor unui pacient cu un terapeut
    List<Evolutie> findAllByPacientKeycloakIdAndTerapeutKeycloakIdOrderByCreatedAtDesc(String pacientKeycloakId, String terapeutKeycloakId);
}