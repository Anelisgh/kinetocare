package com.example.kinetocare.repository;

import com.example.kinetocare.domain.Evolutie;
import com.example.kinetocare.domain.Pacient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvolutieRepository extends JpaRepository<Evolutie, Long> {
    List<Evolutie> findByPacientOrderByDataDesc(Pacient pacient);
}
