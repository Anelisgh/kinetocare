package com.example.chat_service.repository;

import com.example.chat_service.entity.Conversatie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversatieRepository extends JpaRepository<Conversatie, Long> {

    @Query("SELECT c FROM Conversatie c WHERE c.pacientKeycloakId = :keycloakId OR c.terapeutKeycloakId = :keycloakId ORDER BY c.ultimulMesajLa DESC")
    List<Conversatie> findByUserKeycloakIdOrderByUltimulMesajLaDesc(@Param("keycloakId") String keycloakId);

    Optional<Conversatie> findByPacientKeycloakIdAndTerapeutKeycloakId(String pacientKeycloakId, String terapeutKeycloakId);
}
