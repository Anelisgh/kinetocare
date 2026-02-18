package com.example.notificari_service.dto;

import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificareEvent implements Serializable {

    private String tipNotificare;
    private Long userId;
    private String tipUser;       // "PACIENT" sau "TERAPEUT"
    private String titlu;
    private String mesaj;
    private Long entitateLegataId;
    private String tipEntitateLegata; // "PROGRAMARE", "EVALUARE", "JURNAL"
    private String urlActiune;        // URL redirect cand se apasa notificarea
}
