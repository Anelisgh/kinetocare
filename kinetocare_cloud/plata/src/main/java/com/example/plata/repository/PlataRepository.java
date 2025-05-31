package com.example.plata.repository;

import com.example.plata.domain.Plata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlataRepository extends JpaRepository<Plata, Long> {
    boolean existsByProgramareId(Long programareId);
    List<Plata> findByPacientId(Long pacientId);
}
