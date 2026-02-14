package com.example.terapeuti_service.repository;

import com.example.terapeuti_service.entity.ConcediuTerapeut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ConcediuRepository extends JpaRepository<ConcediuTerapeut, Long> {
    // returneaza toate concediile unui terapeut
    List<ConcediuTerapeut> findByTerapeutId(Long terapeutId);

    // returneaza concediile care se suprapun cu perioada specificata
    @Query("SELECT c FROM ConcediuTerapeut c WHERE c.terapeutId = :terapeutId " +
            "AND c.dataSfarsit >= :dataInceput AND c.dataInceput <= :dataSfarsit")
    List<ConcediuTerapeut> findOverlappingConcedii(
            @Param("terapeutId") Long terapeutId,
            @Param("dataInceput") LocalDate dataInceput,
            @Param("dataSfarsit") LocalDate dataSfarsit
    );

    // returneaza concediile viitoare si curente ordonate contrologic
    @Query("SELECT c FROM ConcediuTerapeut c WHERE c.terapeutId = :terapeutId " +
            "AND c.dataSfarsit >= :currentDate ORDER BY c.dataInceput")
    List<ConcediuTerapeut> findFutureAndCurrentConcedii(
            @Param("terapeutId") Long terapeutId,
            @Param("currentDate") LocalDate currentDate
    );

    // returneaza daca exista un concediu care se suprapune cu perioada specificata
    @Query("SELECT COUNT(c) > 0 FROM ConcediuTerapeut c WHERE c.terapeutId = :terapeutId " +
            "AND c.dataInceput <= :dataStart AND c.dataSfarsit >= :dataEnd")
    boolean isTerapeutInConcediu(
            @Param("terapeutId") Long terapeutId,
            @Param("dataStart") LocalDate dataStart,
            @Param("dataEnd") LocalDate dataEnd);
}
