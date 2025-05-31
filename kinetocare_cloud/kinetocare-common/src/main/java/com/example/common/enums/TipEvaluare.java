package com.example.common.enums;

public enum TipEvaluare {
    INITIALA("Inițială"),
    FINALA("Finală");

    private final String displayName;

    TipEvaluare(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
