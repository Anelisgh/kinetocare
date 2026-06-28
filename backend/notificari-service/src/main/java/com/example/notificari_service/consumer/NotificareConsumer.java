package com.example.notificari_service.consumer;

import com.example.notificari_service.config.RabbitMQConfig;
import com.example.notificari_service.dto.NotificareEvent;
import com.example.notificari_service.service.NotificareService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.example.notificari_service.repository.MesajProcesatRepository;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;

// asteapta si cand apare un mesaj in coada il ia instant si-l da mai departe la service
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificareConsumer {

    private final NotificareService notificareService;
    private final MesajProcesatRepository mesajProcesatRepository;

    // Dacă procesarea eșuează, Spring AMQP face NACK automat și mesajul
    // este rutat la Dead Letter Queue (notificari.queue.dead) pentru inspecție/replay.
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void primesteMesaj(NotificareEvent event, @Header(value = AmqpHeaders.MESSAGE_ID, required = false) String messageId) {
        log.info("Mesaj primit din RabbitMQ: {} → userKeycloakId={}, messageId={}", event.tipNotificare(), event.userKeycloakId(), messageId);

        if (messageId != null) {
            try {
                int rowsAffected = mesajProcesatRepository.insertIdempotent(messageId);
                if (rowsAffected == 0) {
                    log.info("Mesaj duplicat detectat și ignorat: messageId={}", messageId);
                    return; // Silently absorb
                }
            } catch (Exception e) {
                log.error("Eroare la verificarea idempotenței pentru messageId={}: {}", messageId, e.getMessage());
                // În caz de eroare de DB la insert, re-aruncăm excepția pentru ca mesajul să fie reîncercat sau trimis în DLQ
                throw e;
            }
        } else {
            log.warn("Mesaj primit fără messageId! Se procesează fără verificare de idempotență.");
        }

        notificareService.proceseazaEveniment(event);
        log.info("Notificare procesată cu succes: {} → userKeycloakId={}", event.tipNotificare(), event.userKeycloakId());
    }
}
