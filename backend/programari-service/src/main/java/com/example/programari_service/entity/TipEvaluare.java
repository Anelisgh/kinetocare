package com.example.programari_service.entity;

public enum TipEvaluare {
    INITIALA("Evaluare Inițială"),
    REEVALUARE("Re-evaluare");

    private final String displayName;

    TipEvaluare(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
