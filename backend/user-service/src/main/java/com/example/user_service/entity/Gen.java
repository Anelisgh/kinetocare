package com.example.user_service.entity;

public enum Gen {
    MASCULIN("Masculin"), FEMININ("Feminin");

    private final String displayName;

    Gen(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
