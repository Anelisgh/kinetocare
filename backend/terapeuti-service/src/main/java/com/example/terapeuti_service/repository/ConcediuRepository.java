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
    List<ConcediuTerapeut> findByTerapeutId(Long terapeutId);

    // Găsește concediile care se suprapun cu perioada specificată
    @Query("SELECT c FROM ConcediuTerapeut c WHERE c.terapeutId = :terapeutId " +
            "AND c.dataSfarsit >= :dataInceput AND c.dataInceput <= :dataSfarsit")
    List<ConcediuTerapeut> findOverlappingConcedii(
            @Param("terapeutId") Long terapeutId,
            @Param("dataInceput") LocalDate dataInceput,
            @Param("dataSfarsit") LocalDate dataSfarsit
    );

    // Găsește concediile viitoare și curente
    @Query("SELECT c FROM ConcediuTerapeut c WHERE c.terapeutId = :terapeutId " +
            "AND c.dataSfarsit >= :currentDate ORDER BY c.dataInceput")
    List<ConcediuTerapeut> findFutureAndCurrentConcedii(
            @Param("terapeutId") Long terapeutId,
            @Param("currentDate") LocalDate currentDate
    );

    boolean existsByTerapeutIdAndDataInceputLessThanEqualAndDataSfarsitGreaterThanEqual(
            Long terapeutId, LocalDate dataInceput, LocalDate dataSfarsit);
}
