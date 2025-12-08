package com.example.programari_service.entity;

public enum StatusProgramare {
    PROGRAMATA("Programată"),
    FINALIZATA("Finalizată"),
    ANULATA("Anulată");

    private final String displayName;

    StatusProgramare(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
