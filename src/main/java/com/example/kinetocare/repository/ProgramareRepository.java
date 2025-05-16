package com.example.kinetocare.repository;

import com.example.kinetocare.domain.Programare;
import com.example.kinetocare.domain.Status;
import com.example.kinetocare.domain.Terapeut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface ProgramareRepository extends JpaRepository<Programare, Long> {
    List<Programare> findByTerapeut(Terapeut terapeut);
    @Query("SELECT p FROM Programare p WHERE " +
            "p.terapeut = :terapeut AND " +
            "p.data = :data AND " +
            "((p.ora <= :oraStart AND p.oraEnd > :oraStart) OR " +
            "(p.ora < :oraEnd AND p.oraEnd >= :oraEnd) OR " +
            "(p.ora >= :oraStart AND p.oraEnd <= :oraEnd))")
    List<Programare> findConflictingAppointments(
            @Param("terapeut") Terapeut terapeut,
            @Param("data") LocalDate data,
            @Param("oraStart") LocalTime oraStart,
            @Param("oraEnd") LocalTime oraEnd
    );
    List<Programare> findByTerapeutAndDataBetween(Terapeut terapeut, LocalDate start, LocalDate end);
    List<Programare> findByDataBeforeAndStatus(LocalDate data, Status status);
    List<Programare> findByStatusAndDataBefore(Status status, LocalDate data);

    @Query("SELECT p FROM Programare p WHERE " +
            "p.terapeut = :terapeut AND " +
            "p.data = :data AND " +
            "p.status <> 'ANULATA' AND " +
            "((p.ora < :oraEnd AND p.oraEnd > :ora))")
    List<Programare> findProgramariSuprapuse(
            @Param("terapeut") Terapeut terapeut,
            @Param("data") LocalDate data,
            @Param("ora") LocalTime ora,
            @Param("oraEnd") LocalTime oraEnd
    );

    // Pentru modificare programare
    @Query("SELECT p FROM Programare p WHERE " +
            "p.id <> :id AND " +
            "p.terapeut = :terapeut AND " +
            "p.data = :data AND " +
            "p.status <> 'ANULATA' AND " +
            "((p.ora < :oraEnd AND p.oraEnd > :ora))")
    List<Programare> findProgramariSuprapuseExcluzandCurrent(
            @Param("id") Long id,
            @Param("terapeut") Terapeut terapeut,
            @Param("data") LocalDate data,
            @Param("ora") LocalTime ora,
            @Param("oraEnd") LocalTime oraEnd
    );
    // Pentru a incarca relația cu terapeut
    @Query("SELECT p FROM Programare p LEFT JOIN FETCH p.terapeut WHERE p.id = :id")
    Optional<Programare> findByIdWithTerapeut(@Param("id") Long id);
}
