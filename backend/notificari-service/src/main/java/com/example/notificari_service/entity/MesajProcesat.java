package com.example.notificari_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mesaje_procesate")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MesajProcesat {

    @Id
    @Column(name = "message_id", length = 36, nullable = false)
    private String messageId;

    @Column(name = "processed_at", nullable = false)
    private LocalDateTime processedAt;
}
