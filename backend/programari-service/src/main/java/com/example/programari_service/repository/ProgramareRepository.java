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
import java.util.Optional;

public interface ProgramareRepository extends JpaRepository<Programare, Long> {
        // prima programare viitoare activa
        @Query("SELECT p FROM Programare p WHERE p.pacientId = :pacientId " +
                        "AND p.status = 'PROGRAMATA' " +
                        "AND (p.data > CURRENT_DATE OR (p.data = CURRENT_DATE AND p.oraInceput > CURRENT_TIME)) " +
                        "ORDER BY p.data ASC, p.oraInceput ASC")
        List<Programare> gasesteUrmatoareaProgramare(@Param("pacientId") Long pacientId, PageRequest pageRequest);

        // numara programarile active pentru un pacient si terapeut
        // pentru primaIntalnire
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
                        @Param("oraSfarsitNoua") LocalTime oraSfarsitNoua);

        // gaseste programarile unui terapeut intr-o zi specifica cu un anumit status
        List<Programare> findByTerapeutIdAndDataAndStatus(Long terapeutId, LocalDate data,
                        StatusProgramare statusProgramare);

        // numara sedintele finalizate dupa o anumita data
        @Query("SELECT COUNT(p) FROM Programare p WHERE p.pacientId = :pId AND p.terapeutId = :tId AND p.status = 'FINALIZATA' AND p.data > :dataRef")
        long countSedinteDupaData(Long pId, Long tId, LocalDate dataRef);

        // numara programarile active sau finalizate
        @Query("SELECT COUNT(p) FROM Programare p WHERE p.pacientId = :pId AND p.terapeutId = :tId AND p.status IN ('PROGRAMATA', 'FINALIZATA')")
        long countProgramariActiveSauFinalizate(Long pId, Long tId);

        // numara sedintele finalizate ale unui pacient dupa o anumita data
        @Query("SELECT COUNT(p) FROM Programare p " +
                        "WHERE p.pacientId = :pId " +
                        "AND p.status = 'FINALIZATA' " +
                        "AND p.data > :dataRef")
        long countSedintePacientDupaData(@Param("pId") Long pId, @Param("dataRef") LocalDate dataRef);

        // extrage toate programarile unui terapeut intr-un interval de timp
        List<Programare> findAllByTerapeutIdAndDataBetween(Long terapeutId, LocalDate startDate, LocalDate endDate);

        // pentru CRON JOB
        // gaseste programarile expirate care nu au fost inca finalizate
        @Query("SELECT p FROM Programare p WHERE p.status = 'PROGRAMATA' AND (p.data < :currentDate OR (p.data = :currentDate AND p.oraSfarsit < :currentTime))")

        List<Programare> findExpiredAppointments(@Param("currentDate") LocalDate currentDate,
                        @Param("currentTime") LocalTime currentTime);

        // pentru dropdown-ul din evaluari. gaseste toti pacientii cu care terapeutul are programari
        @Query("SELECT DISTINCT p.pacientId FROM Programare p WHERE p.terapeutId = :terapeutId")
        List<Long> findPacientiIdByTerapeutId(@Param("terapeutId") Long terapeutId);

        // gaseste ultimele programari finalizate dintre un pacient si un terapeut
        @Query("SELECT p FROM Programare p " +
                        "WHERE p.pacientId = :pacientId " +
                        "AND p.terapeutId = :terapeutId " +
                        "AND p.status = 'FINALIZATA' " +
                        "ORDER BY p.data DESC, p.oraInceput DESC")
        List<Programare> findLatestAppointments(@Param("pacientId") Long pacientId,
                        @Param("terapeutId") Long terapeutId,
                        PageRequest pageable);

        // gaseste sedintele finalizate fara jurnal
        @Query("SELECT p FROM Programare p WHERE p.pacientId = :pacientId AND p.status = :status AND p.areJurnal = false ORDER BY p.data DESC")
        List<Programare> findByPacientIdAndStatusAndAreJurnalFalseOrderByDataDesc(@Param("pacientId") Long pacientId,
                        @Param("status") StatusProgramare status);
}
