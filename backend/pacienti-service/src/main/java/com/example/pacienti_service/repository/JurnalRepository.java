package com.example.pacienti_service.repository;

import com.example.pacienti_service.entity.JurnalPacient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JurnalRepository extends JpaRepository<JurnalPacient, Long> {
    // jurnalele unui pacient ordonate descrescator dupa data (cel mai recent la cel mai vechi)
    List<JurnalPacient> findByPacientIdOrderByDataDesc(Long pacientId);
}