package com.example.terapeuti_service.repository;

import com.example.terapeuti_service.entity.DisponibilitateTerapeut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DisponibilitateRepository extends JpaRepository<DisponibilitateTerapeut, Long> {
    List<DisponibilitateTerapeut> findByTerapeutIdAndActiveTrue(Long terapeutId);
    List<DisponibilitateTerapeut> findByTerapeutId(Long terapeutId);
// disponibilitatile care se suprapun
    @Query("SELECT d FROM DisponibilitateTerapeut d " +
            "WHERE d.active = true " +
            "AND d.terapeutId = :terapeutId " +
            "AND d.locatieId = :locatieId " +
            "AND d.ziSaptamana = :ziSaptamana " +
            "AND d.oraInceput < :oraSfarsit " +    // (StartA < EndB)
            "AND d.oraSfarsit > :oraInceput")      // (EndA > StartB)
    List<DisponibilitateTerapeut> findOverlappingDisponibilitate(
            @Param("terapeutId") Long terapeutId,
            @Param("locatieId") Long locatieId,
            @Param("ziSaptamana") Integer ziSaptamana,
            @Param("oraInceput") LocalTime oraInceput,
            @Param("oraSfarsit") LocalTime oraSfarsit
    );
    List<DisponibilitateTerapeut> findByTerapeutIdInAndActiveTrue(List<Long> terapeutIds);

    Optional<DisponibilitateTerapeut> findByTerapeutIdAndLocatieIdAndZiSaptamana(
            Long terapeutId, Long locatieId, Integer ziSaptamana);


}
