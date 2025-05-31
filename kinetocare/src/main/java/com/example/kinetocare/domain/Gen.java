package com.example.kinetocare.domain;

public enum Gen {
    M("Masculin"), F("Feminin");

    private final String displayName;

    Gen(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
