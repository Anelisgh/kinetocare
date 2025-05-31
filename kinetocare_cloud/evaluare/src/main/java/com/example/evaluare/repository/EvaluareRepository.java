package com.example.evaluare.repository;

import com.example.evaluare.domain.Evaluare;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvaluareRepository extends JpaRepository<Evaluare, Long>  {
    List<Evaluare> findByTerapeutId(Long terapeutId);
}
