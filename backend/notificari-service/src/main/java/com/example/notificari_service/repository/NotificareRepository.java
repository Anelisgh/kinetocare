package com.example.notificari_service.repository;

import com.example.notificari_service.entity.Notificare;
import com.example.notificari_service.entity.TipUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificareRepository extends JpaRepository<Notificare, Long> {

    // lista notificarile unui user, ordonate descrescator dupa data crearii
    List<Notificare> findByUserIdAndTipUserOrderByCreatedAtDesc(Long userId, TipUser tipUser);

    // numara notificarile necitite
    long countByUserIdAndTipUserAndEsteCititaFalse(Long userId, TipUser tipUser);

    // lista notificarile necitite (pentru mark all as read)
    List<Notificare> findByUserIdAndTipUserAndEsteCititaFalse(Long userId, TipUser tipUser);

    @Modifying
    @Query("UPDATE Notificare n SET n.esteCitita = true, n.cititaLa = CURRENT_TIMESTAMP WHERE n.userId = :userId AND n.tipUser = :tipUser AND n.esteCitita = false")
    int markAllAsReadByUserIdAndTipUser(Long userId, TipUser tipUser);
}
