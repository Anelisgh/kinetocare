package com.example.terapeuti_service.repository;

import com.example.terapeuti_service.entity.Locatie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocatieRepository extends JpaRepository<Locatie, Long> {
    List<Locatie> findByActiveTrue();
}