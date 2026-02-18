package com.example.notificari_service.repository;

import com.example.notificari_service.entity.Notificare;
import com.example.notificari_service.entity.TipUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificareRepository extends JpaRepository<Notificare, Long> {

    // lista notificarile unui user, ordonate descrescator dupa data crearii
    List<Notificare> findByUserIdAndTipUserOrderByCreatedAtDesc(Long userId, TipUser tipUser);

    // numara notificarile necitite
    long countByUserIdAndTipUserAndEsteCititaFalse(Long userId, TipUser tipUser);

    // lista notificarile necitite (pentru mark all as read)
    List<Notificare> findByUserIdAndTipUserAndEsteCititaFalse(Long userId, TipUser tipUser);
}
