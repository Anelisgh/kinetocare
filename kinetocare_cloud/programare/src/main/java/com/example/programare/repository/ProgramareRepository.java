package com.example.programare.repository;

import com.example.common.enums.Status;
import com.example.programare.domain.Programare;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ProgramareRepository extends JpaRepository<Programare, Long> {
    List<Programare> findByPacientId(Long pacientId);
    List<Programare> findByPacientIdAndDataGreaterThanEqualAndStatus(Long pacientId, LocalDate data, Status status);
}
