package com.example.terapeuti_service.entity;

public enum Specializare {
    ADULTI("Adulți"),
    PEDIATRIE("Pediatrie");

    private final String displayName;

    Specializare(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
