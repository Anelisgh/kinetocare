package com.example.chat_service.entity;

public enum TipExpeditor {
    PACIENT("Pacient"),
    TERAPEUT("Terapeut");

    private final String displayName;

    TipExpeditor(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
