package com.example.notificari_service.repository;

import com.example.notificari_service.entity.MesajProcesat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface MesajProcesatRepository extends JpaRepository<MesajProcesat, String> {

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO mesaje_procesate (message_id, processed_at) VALUES (:messageId, NOW()) ON DUPLICATE KEY UPDATE message_id = message_id", nativeQuery = true)
    int insertIdempotent(@Param("messageId") String messageId);
}
