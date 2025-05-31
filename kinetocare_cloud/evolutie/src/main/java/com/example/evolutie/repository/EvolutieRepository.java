package com.example.evolutie.repository;

import com.example.evolutie.domain.Evolutie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvolutieRepository extends JpaRepository<Evolutie, Long> {
    List<Evolutie> findByTerapeutId(Long terapeutId);
}
