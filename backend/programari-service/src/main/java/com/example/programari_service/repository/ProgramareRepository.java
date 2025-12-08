package com.example.programari_service.repository;

import com.example.programari_service.entity.Programare;
import com.example.programari_service.entity.StatusProgramare;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ProgramareRepository extends JpaRepository<Programare,Long> {
    // prima programare viitoare activa
    @Query("SELECT p FROM Programare p WHERE p.pacientId = :pacientId " +
            "AND p.status = 'PROGRAMATA' " +
            "AND (p.data > CURRENT_DATE OR (p.data = CURRENT_DATE AND p.oraInceput > CURRENT_TIME)) " +
            "ORDER BY p.data ASC, p.oraInceput ASC")
    List<Programare> gasesteUrmatoareaProgramare(@Param("pacientId") Long pacientId, PageRequest pageRequest);

    // numara programarile active pentru un pacient si terapeut pentru primaIntalnire
    long countByPacientIdAndTerapeutId(Long pacientId, Long terapeutId);

    // verifica suprapunerea programarilor
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
            "FROM Programare p " +
            "WHERE p.terapeutId = :terapeutId " +
            "AND p.data = :data " +
            "AND p.status = 'PROGRAMATA' " + // ignoram programarile anulate
            "AND p.oraInceput < :oraSfarsitNoua " +
            "AND p.oraSfarsit > :oraInceputNoua")
    boolean existaSuprapunere(
            @Param("terapeutId") Long terapeutId,
            @Param("data") LocalDate data,
            @Param("oraInceputNoua") LocalTime oraInceputNoua,
            @Param("oraSfarsitNoua") LocalTime oraSfarsitNoua
    );

    List<Programare> findByTerapeutIdAndDataAndStatus(Long terapeutId, LocalDate data, StatusProgramare statusProgramare);

    // numara sedintele finalizate dupa o anumita data
    @Query("SELECT COUNT(p) FROM Programare p WHERE p.pacientId = :pId AND p.terapeutId = :tId AND p.status = 'FINALIZATA' AND p.data > :dataRef")
    long countSedinteDupaData(Long pId, Long tId, LocalDate dataRef);

    @Query("SELECT COUNT(p) FROM Programare p WHERE p.pacientId = :pId AND p.terapeutId = :tId AND p.status IN ('PROGRAMATA', 'FINALIZATA')")
    long countProgramariActiveSauFinalizate(Long pId, Long tId);

    @Query("SELECT COUNT(p) FROM Programare p " +
            "WHERE p.pacientId = :pId " +
            "AND p.status = 'FINALIZATA' " +
            "AND p.data > :dataRef")
    long countSedintePacientDupaData(@Param("pId") Long pId, @Param("dataRef") LocalDate dataRef);
}
