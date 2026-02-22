package com.example.programari_service.dto;

import java.io.Serializable;

public record NotificareEvent(
    String tipNotificare,
    Long userId,
    String tipUser,
    String titlu,
    String mesaj,
    Long entitateLegataId,
    String tipEntitateLegata,
    String urlActiune
) implements Serializable {} 
