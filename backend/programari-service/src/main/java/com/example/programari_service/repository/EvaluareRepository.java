package com.example.programari_service.repository;

import com.example.programari_service.entity.Evaluare;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EvaluareRepository extends JpaRepository<Evaluare, Long> {
    // gaseste ultima evaluare, sortand dupa data descrescator
    Optional<Evaluare> findFirstByPacientKeycloakIdOrderByDataDesc(String pacientKeycloakId);

    // gaseste evaluarea unei programari specifice
    Optional<Evaluare> findByProgramareId(Long programareId);

    // toate evaluarile unui pacient (de la toti terapeutii), ordonate desc
    List<Evaluare> findAllByPacientKeycloakIdOrderByDataDesc(String pacientKeycloakId);
}
