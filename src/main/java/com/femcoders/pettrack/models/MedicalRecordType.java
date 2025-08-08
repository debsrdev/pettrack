package com.femcoders.pettrack.models;

public enum MedicalRecordType {
    VACCINATION("Vacunación"),
    REVISION("Revisión"),
    SURGERY("Cirugía");

    private final String displayName;

    MedicalRecordType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
