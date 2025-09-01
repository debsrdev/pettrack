package com.femcoders.pettrack.dtos.medicalRecord;

import com.femcoders.pettrack.models.MedicalRecordType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record MedicalRecordRequest(
        @NotBlank(message = "Description is required")
        @Size(max = 200, message = "Description must be less than 200 characters")
        String description,

        @NotNull(message = "Weight is required")
        double weight,

        @NotNull(message = "Date is required")
        @PastOrPresent(message = "Birth date must be in the past or today")
        LocalDate date,

        @Enumerated(EnumType.STRING)
        @NotNull(message = "Type is required")
        MedicalRecordType type,

        @NotNull(message = "Pet ID is required")
        Long petId
) {
}
