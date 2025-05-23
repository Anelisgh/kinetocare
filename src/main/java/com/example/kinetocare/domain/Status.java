package com.example.kinetocare.domain;

public enum Status {
    PROGRAMATA("Programată"), ANULATA("Anulată"), FINALIZATA("Finalizată");

    private final String displayName;

    Status(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
