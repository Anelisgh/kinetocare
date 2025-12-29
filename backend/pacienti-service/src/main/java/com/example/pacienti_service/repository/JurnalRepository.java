package com.example.pacienti_service.repository;

import com.example.pacienti_service.entity.JurnalPacient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JurnalRepository extends JpaRepository<JurnalPacient, Long> {
    // jurnalele unui pacient ordonate descrescator dupa data
    List<JurnalPacient> findByPacientIdOrderByDataDesc(Long pacientId);
}
