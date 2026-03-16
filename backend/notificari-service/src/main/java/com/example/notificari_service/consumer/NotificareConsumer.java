package com.example.notificari_service.consumer;

import com.example.notificari_service.config.RabbitMQConfig;
import com.example.notificari_service.dto.NotificareEvent;
import com.example.notificari_service.service.NotificareService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

// asteapta si cand apare un mesaj in coada il ia instant si-l da mai departe la service
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificareConsumer {

    private final NotificareService notificareService;

    // Dacă procesarea eșuează, Spring AMQP face NACK automat și mesajul
    // este rutat la Dead Letter Queue (notificari.queue.dead) pentru inspecție/replay.
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void primesteMesaj(NotificareEvent event) {
        log.info("Mesaj primit din RabbitMQ: {} → userKeycloakId={}", event.tipNotificare(), event.userKeycloakId());
        notificareService.proceseazaEveniment(event);
        log.info("Notificare procesată cu succes: {} → userKeycloakId={}", event.tipNotificare(), event.userKeycloakId());
    }
}
