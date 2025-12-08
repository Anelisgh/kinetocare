package com.example.programari_service.repository;

import com.example.programari_service.entity.Evaluare;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EvaluareRepository extends JpaRepository<Evaluare, Long> {
    // gaseste ultima evaluare, sortand dupa data descrescator
    Optional<Evaluare> findFirstByPacientIdOrderByDataDesc(Long pacientId);
}
