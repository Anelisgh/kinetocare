package com.example.kinetocare.repository;

import com.example.kinetocare.domain.Pacient;
import com.example.kinetocare.domain.Plata;
import com.example.kinetocare.domain.Programare;
import com.example.kinetocare.domain.StarePlata;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.util.List;

public interface PlataRepository extends JpaRepository<Plata, Long> {

    @Query("SELECT p FROM Plata p " +
            "LEFT JOIN FETCH p.programare pr " +
            "LEFT JOIN FETCH pr.serviciu " +
            "WHERE p.pacient.id = :pacientId " +
            "ORDER BY p.data DESC")
    List<Plata> findAllByPacientIdWithDetails(@Param("pacientId") Long pacientId);
    boolean existsByProgramare(Programare programare);
    Page<Plata> findAllByPacient(Pacient pacient, Pageable pageable);
    @Query("SELECT SUM(p.suma) FROM Plata p WHERE p.pacient = :pacient AND p.starePlata = :stare")
    BigDecimal sumByPacientAndStarePlata(@Param("pacient") Pacient pacient, @Param("stare") StarePlata stare);
}
