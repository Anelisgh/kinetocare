package com.example.pacienti_service.service;

import com.example.pacienti_service.config.RabbitMQConfig;
import com.example.pacienti_service.dto.NotificareEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificarePublisher {

    private final RabbitTemplate rabbitTemplate;

    // cand pacientul completeaza jurnalul, notificam terapeutul
    public void jurnalCompletat(Long terapeutId, Long pacientId, Long programareId) {
        NotificareEvent event = NotificareEvent.builder()
                .tipNotificare("JURNAL_COMPLETAT")
                .userId(terapeutId)
                .tipUser("TERAPEUT")
                .titlu("Jurnal completat")
                .mesaj("Un pacient a completat jurnalul pentru o ședință.")
                .entitateLegataId(programareId)
                .tipEntitateLegata("PROGRAMARE")
                .urlActiune("/fisa-pacient/" + pacientId)
                .build();

        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME,
                    "notificare.jurnal.completat", event);
            log.info("Notificare trimisă: JURNAL_COMPLETAT → terapeutId={}", terapeutId);
        } catch (Exception e) {
            log.error("Eroare la trimiterea notificării JURNAL_COMPLETAT: {}", e.getMessage());
        }
    }
}
