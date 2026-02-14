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
    // returneaza disponibilitatile active ale unui terapeut
    List<DisponibilitateTerapeut> findByTerapeutIdAndActiveTrue(Long terapeutId);
    // returneaza toate disponibilitatile unui terapeut (active si inactive)
    List<DisponibilitateTerapeut> findByTerapeutId(Long terapeutId);
    // returneaza disponibilitatile care se suprapun (aceeasi zi, locatie si interval oral)
    @Query("SELECT d FROM DisponibilitateTerapeut d " +
            "WHERE d.active = true " +
            "AND d.terapeutId = :terapeutId " +
            "AND d.locatieId = :locatieId " +
            "AND d.ziSaptamana = :ziSaptamana " +
            "AND d.oraInceput < :oraSfarsit " +
            "AND d.oraSfarsit > :oraInceput")
    List<DisponibilitateTerapeut> findOverlappingDisponibilitate(
            @Param("terapeutId") Long terapeutId,
            @Param("locatieId") Long locatieId,
            @Param("ziSaptamana") Integer ziSaptamana,
            @Param("oraInceput") LocalTime oraInceput,
            @Param("oraSfarsit") LocalTime oraSfarsit
    );
    // returneaza disponibilitatile active ale terapeutilor dintr-o lista de id-uri
    List<DisponibilitateTerapeut> findByTerapeutIdInAndActiveTrue(List<Long> terapeutIds);
    // returneaza disponibilitatea pentru un terapeut cu o anumita locatie si o anumita zi a saptamanii
    Optional<DisponibilitateTerapeut> findByTerapeutIdAndLocatieIdAndZiSaptamana(
            Long terapeutId, Long locatieId, Integer ziSaptamana);


}
