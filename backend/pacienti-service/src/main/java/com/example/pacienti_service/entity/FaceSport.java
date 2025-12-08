package com.example.pacienti_service.entity;

public enum FaceSport {
    DA("Da"), NU("Nu");

    private final String displayName;

    FaceSport(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
