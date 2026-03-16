package com.example.notificari_service.consumer;

import com.example.notificari_service.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * Consumer pentru Dead Letter Queue (DLQ).
 *
 * Mesajele ajung aici dacă NotificareConsumer aruncă o excepție la procesare
 * (ex: DB down, eveniment malformat, eroare de business).
 *
 * IMPORTANT: Nu aruncăm excepții din acest consumer — altfel mesajul
 * ar intra în buclă infinită (DLQ → consumer → DLQ → ...).
 * Tot ce facem este să logăm pentru inspecție manuală + monitorizare.
 */
@Slf4j
@Component
public class DeadLetterConsumer {

    @RabbitListener(queues = RabbitMQConfig.DLQ_NAME)
    public void processeazaMesajEsuat(Message message) {
        try {
            String body = new String(message.getBody(), StandardCharsets.UTF_8);
            log.error(
                "[DLQ] Mesaj eșuat primit în Dead Letter Queue. " +
                "Body: {} | Headers: {}",
                body,
                message.getMessageProperties().getHeaders()
            );
        } catch (Exception e) {
            // Nu re-aruncăm excepția — ar provoca buclă infinită în DLQ
            log.error("[DLQ] Eroare la logarea mesajului din DLQ: {}", e.getMessage());
        }
    }
}
