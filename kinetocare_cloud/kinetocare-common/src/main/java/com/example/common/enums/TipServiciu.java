package com.example.common.enums;

public enum TipServiciu {
    EVALUARE("Evaluare"),
    REEVALUARE("Reevaluare"),
    OSTEOPATIE("Osteopatie"),
    TERAPIE_MOTORIE("Terapie motorie"),
    LOGOPEDIE("Logopedie"),
    MASAJ("Masaj");

    private final String displayName;

    TipServiciu(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
