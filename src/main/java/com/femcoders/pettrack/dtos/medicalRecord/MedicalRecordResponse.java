package com.femcoders.pettrack.dtos.medicalRecord;

import com.femcoders.pettrack.models.MedicalRecordType;

import java.time.LocalDate;

public record MedicalRecordResponse(
        Long id,
        String description,
        double weight,
        LocalDate date,
        MedicalRecordType type,
        String petName,
        String createdBy
) {
}
