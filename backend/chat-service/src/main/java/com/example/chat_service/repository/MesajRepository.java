package com.example.chat_service.repository;

import com.example.chat_service.entity.Mesaj;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MesajRepository extends JpaRepository<Mesaj, Long> {

    List<Mesaj> findByConversatieIdOrderByTrimisLaAsc(Long conversatieId);

    @Query("SELECT m FROM Mesaj m WHERE m.conversatieId = :conversatieId ORDER BY m.trimisLa DESC LIMIT 1")
    Optional<Mesaj> findTopByConversatieIdOrderByTrimisLaDesc(@Param("conversatieId") Long conversatieId);

    @Modifying
    @Query("UPDATE Mesaj m SET m.esteCitit = true, m.cititLa = :cititLa WHERE m.conversatieId = :conversatieId AND m.expeditorKeycloakId != :keycloakId AND m.esteCitit = false")
    int markMessagesAsRead(@Param("conversatieId") Long conversatieId, @Param("keycloakId") String keycloakId, @Param("cititLa") OffsetDateTime cititLa);
}
