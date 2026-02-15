package com.example.servicii_service.repository;

import com.example.servicii_service.entity.Serviciu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiciuRepository extends JpaRepository<Serviciu, Long> {
    List<Serviciu> findByTipServiciu_NumeContainingIgnoreCase(String numeTip);
    
    // Cautare dupa nume specific
    List<Serviciu> findByNumeContainingIgnoreCase(String nume);
}
