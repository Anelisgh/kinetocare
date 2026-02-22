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
        // prima programare viitoare activa
        @Query("SELECT p FROM Programare p WHERE p.pacientId = :pacientId " +
                        "AND p.status = 'PROGRAMATA' " +
                        "AND (p.data > :dataAzi OR (p.data = :dataAzi AND p.oraInceput > :oraCurenta)) " +
                        "ORDER BY p.data ASC, p.oraInceput ASC")
        List<Programare> gasesteUrmatoareaProgramare(@Param("pacientId") Long pacientId, @Param("dataAzi") LocalDate dataAzi, @Param("oraCurenta") LocalTime oraCurenta, PageRequest pageRequest);

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

        // gaseste toate programarile unui pacient, ordonate descrescator dupa data si ora
        List<Programare> findAllByPacientIdOrderByDataDescOraInceputDesc(Long pacientId);

        // pentru REMINDERE - gaseste programarile programate intr-o fereastra de timp
        @Query("SELECT p FROM Programare p WHERE p.status = 'PROGRAMATA' " +
                        "AND p.data = :data AND p.oraInceput BETWEEN :oraStart AND :oraEnd")
        List<Programare> findProgramariInFereastra(@Param("data") LocalDate data,
                        @Param("oraStart") LocalTime oraStart,
                        @Param("oraEnd") LocalTime oraEnd);

        // admin cancel - gaseste programarile viitoare programate ale unui terapeut
        @Query("SELECT p FROM Programare p WHERE p.terapeutId = :terapeutId " +
                        "AND p.status = :status " +
                        "AND (p.data > :data OR (p.data = :data AND p.oraInceput > :ora))")
        List<Programare> findByTerapeutIdAndStatusAndDataGreaterThanEqual(
                        @Param("terapeutId") Long terapeutId, @Param("status") StatusProgramare status, @Param("data") LocalDate data, @Param("ora") LocalTime ora);

        // admin cancel - gaseste programarile viitoare programate ale unui pacient
        @Query("SELECT p FROM Programare p WHERE p.pacientId = :pacientId " +
                        "AND p.status = :status " +
                        "AND (p.data > :data OR (p.data = :data AND p.oraInceput > :ora))")
        List<Programare> findByPacientIdAndStatusAndDataGreaterThanEqual(
                        @Param("pacientId") Long pacientId, @Param("status") StatusProgramare status, @Param("data") LocalDate data, @Param("ora") LocalTime ora);

        // schimbare terapeut - gaseste programarile viitoare dintre un pacient si un terapeut 
        @Query("SELECT p FROM Programare p WHERE p.pacientId = :pacientId " +
                        "AND p.terapeutId = :terapeutId " +
                        "AND p.status = :status " +
                        "AND (p.data > :data OR (p.data = :data AND p.oraInceput > :ora))")
        List<Programare> findByPacientIdAndTerapeutIdAndStatusAndDataGreaterThanEqual(
                        @Param("pacientId") Long pacientId, @Param("terapeutId") Long terapeutId, @Param("status") StatusProgramare status, @Param("data") LocalDate data, @Param("ora") LocalTime ora);

        // ----------------- STATISTICI -----------------

        // 1. Programari pe luna per locatie
        @Query("SELECT new com.example.programari_service.dto.statistici.StatisticiProgramariLunareDTO(" +
                "p.locatieId, '', YEAR(p.data), MONTH(p.data), COUNT(p)) " +
                "FROM Programare p " +
                "WHERE p.status <> :status " +
                "AND p.data BETWEEN :startDate AND :endDate " +
                "GROUP BY p.locatieId, YEAR(p.data), MONTH(p.data)")
        List<com.example.programari_service.dto.statistici.StatisticiProgramariLunareDTO> countByLocatieIdAndMonth(
                @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("status") StatusProgramare status);

        // 2. Venituri totale per locatie
        @Query("SELECT new com.example.programari_service.dto.statistici.StatisticiVenituriLocatieDTO(" +
                "p.locatieId, '', SUM(p.pret)) " +
                "FROM Programare p " +
                "WHERE p.status = :status " +
                "AND p.data BETWEEN :startDate AND :endDate " +
                "GROUP BY p.locatieId")
        List<com.example.programari_service.dto.statistici.StatisticiVenituriLocatieDTO> sumPretByLocatieIdAndStatus(
                @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("status") StatusProgramare status);

        // 4. Rata de anulari per locatie - Count All
        @Query("SELECT p.locatieId as locatieId, COUNT(p) as count " +
                "FROM Programare p " +
                "WHERE p.data BETWEEN :startDate AND :endDate " +
                "GROUP BY p.locatieId")
        List<Object[]> countTotalByLocatieId(
                @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

        // 4. Rata de anulari per locatie - Count Anulate
        @Query("SELECT p.locatieId as locatieId, COUNT(p) as count " +
                "FROM Programare p " +
                "WHERE p.status = :status " +
                "AND p.data BETWEEN :startDate AND :endDate " +
                "GROUP BY p.locatieId")
        List<Object[]> countAnulateByLocatieId(
                @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("status") StatusProgramare status);


        // 5. Pacienti noi pe luna per locatie
        @Query("SELECT new com.example.programari_service.dto.statistici.StatisticiPacientiNoiDTO(" +
                "p.locatieId, '', YEAR(p.data), MONTH(p.data), COUNT(DISTINCT p.pacientId)) " +
                "FROM Programare p " +
                "WHERE p.data = (SELECT MIN(p2.data) FROM Programare p2 WHERE p2.pacientId = p.pacientId) " +
                "AND p.data BETWEEN :startDate AND :endDate " +
                "GROUP BY p.locatieId, YEAR(p.data), MONTH(p.data)")
        List<com.example.programari_service.dto.statistici.StatisticiPacientiNoiDTO> countNewPatientsByLocatieIdAndMonth(
                @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

        // 6. Programari per terapeut
        @Query("SELECT new com.example.programari_service.dto.statistici.StatisticiTerapeutDTO(" +
                "p.terapeutId, '', COUNT(p)) " +
                "FROM Programare p " +
                "WHERE p.status <> :status " +
                "AND p.data BETWEEN :startDate AND :endDate " +
                "GROUP BY p.terapeutId")
        List<com.example.programari_service.dto.statistici.StatisticiTerapeutDTO> countByTerapeutIdAndMonth(
                @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("status") StatusProgramare status);
}
