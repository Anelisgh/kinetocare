package com.example.notificari_service.dto;

import com.example.notificari_service.entity.TipNotificare;
import java.time.OffsetDateTime;

public record NotificareDTO(
        Long id,
        TipNotificare tipNotificare,
        String titlu,
        String mesaj,
        String urlActiune,
        Boolean esteCitita,
        OffsetDateTime createdAt
) {
}
