package com.example.notificari_service.repository;

import com.example.notificari_service.entity.Notificare;
import com.example.notificari_service.entity.TipUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificareRepository extends JpaRepository<Notificare, Long> {

    // lista notificari ale unui user, ordonate descrescator dupa data crearii
    List<Notificare> findByUserKeycloakIdAndTipUserOrderByCreatedAtDesc(String userKeycloakId, TipUser tipUser);

    // numara notificarile necitite
    long countByUserKeycloakIdAndTipUserAndEsteCititaFalse(String userKeycloakId, TipUser tipUser);

    // lista notificarile necitite (pentru mark all as read)
    List<Notificare> findByUserKeycloakIdAndTipUserAndEsteCititaFalse(String userKeycloakId, TipUser tipUser);

    @Modifying
    @Query("UPDATE Notificare n SET n.esteCitita = true, n.cititaLa = CURRENT_TIMESTAMP WHERE n.userKeycloakId = :userKeycloakId AND n.tipUser = :tipUser AND n.esteCitita = false")
    int markAllAsReadByUserKeycloakIdAndTipUser(@Param("userKeycloakId") String userKeycloakId, @Param("tipUser") TipUser tipUser);
}
