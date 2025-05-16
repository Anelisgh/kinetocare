package com.example.kinetocare.domain;

public enum TipSport {
    NU_FAC_SPORT("Nu fac sport"),
    DE_PERFORMANTA("De performanță"),
    HOBBY("Hobby");

    private final String displayName;

    TipSport(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

