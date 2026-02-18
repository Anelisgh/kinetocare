package com.example.programari_service.entity;

public enum MotivAnulare {
    ANULAT_DE_PACIENT("Anulat de pacient"),
    ANULAT_DE_TERAPEUT("Anulat de terapeut"),
    NEPREZENTARE("Neprezentare"),
    ADMINISTRATIV("Administrativ");

    private final String displayName;

    MotivAnulare(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}