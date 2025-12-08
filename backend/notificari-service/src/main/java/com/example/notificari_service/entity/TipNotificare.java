package com.example.notificari_service.entity;

public enum TipNotificare {
    // Pacient
    PROGRAMARE_ANULATA_DE_TERAPEUT("Programare anulată de terapeut"),
    REMINDER_24H("Reminder 24h înainte"),
    REMINDER_2H("Reminder 2h înainte"),
    MESAJ_DE_LA_TERAPEUT("Mesaj nou de la terapeut"),
    REMINDER_JURNAL("Completează jurnalul"),
    REEVALUARE_RECOMANDATA("Re-evaluare recomandată"),

    // Terapeut
    PROGRAMARE_NOUA("Programare nouă"),
    EVALUARE_INITIALA_NOUA("Cerere evaluare inițială"),
    PROGRAMARE_ANULATA_DE_PACIENT("Programare anulată de pacient"),
    JURNAL_COMPLETAT("Jurnal completat"),
    MESAJ_DE_LA_PACIENT("Mesaj nou de la pacient"),
    REEVALUARE_NECESARA("Pacient necesită re-evaluare");

    private final String displayName;

    TipNotificare(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
