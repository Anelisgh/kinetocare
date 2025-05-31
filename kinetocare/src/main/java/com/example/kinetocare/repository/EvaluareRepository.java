package com.example.kinetocare.repository;

import com.example.kinetocare.domain.Evaluare;
import com.example.kinetocare.domain.Pacient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvaluareRepository extends JpaRepository<Evaluare, Long> {
    List<Evaluare> findByDiagnosticPacientOrderByDataDesc(Pacient pacient);
}
