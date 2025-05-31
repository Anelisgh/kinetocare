package com.example.common.enums;

public enum StarePlata {
    ACHITATA("Achitată"),
    IN_ASTEPTARE("În așteptare");

    private final String displayName;

    StarePlata(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
