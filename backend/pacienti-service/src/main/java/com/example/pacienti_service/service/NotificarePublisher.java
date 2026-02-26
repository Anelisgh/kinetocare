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
    // terapeutKeycloakId vine acum direct din ProgramareJurnalDTO (populat de programari-service)
    public void jurnalCompletat(String terapeutKeycloakId, Long pacientId, Long programareId) {
        if (terapeutKeycloakId == null) {
            log.warn("Nu s-a putut trimite notificarea JURNAL_COMPLETAT: terapeutKeycloakId este null pt programareId={}", programareId);
            return;
        }
        NotificareEvent event = NotificareEvent.builder()
                .tipNotificare("JURNAL_COMPLETAT")
                .userKeycloakId(terapeutKeycloakId)
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
            log.info("Notificare trimisă: JURNAL_COMPLETAT → terapeutKeycloakId={}", terapeutKeycloakId);
        } catch (Exception e) {
            log.error("Eroare la trimiterea notificării JURNAL_COMPLETAT: {}", e.getMessage());
        }
    }
}
