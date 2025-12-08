package com.example.user_service.entity;

public enum UserRole {
    ADMIN("admin"),
    TERAPEUT("terapeut"),
    PACIENT("pacient");

    private final String keycloakName;

    UserRole(String keycloakName) {
        this.keycloakName = keycloakName;
    }

    public String keycloakName() {
        return keycloakName;
    }
}

