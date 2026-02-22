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

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void primesteMesaj(NotificareEvent event) {
        log.info("Mesaj primit din RabbitMQ: {} → userId={}", event.tipNotificare(), event.userId());
        try {
            notificareService.proceseazaEveniment(event);
        } catch (Exception e) {
            log.error("Eroare la procesarea notificării: {}", e.getMessage(), e);
        }
    }
}
