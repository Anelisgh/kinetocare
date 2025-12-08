package com.example.notificari_service.entity;

public enum TipUser {
    PACIENT("Pacient"),
    TERAPEUT("Terapeut");

    private final String displayName;

    TipUser(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
