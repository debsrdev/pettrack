package com.femcoders.pettrack.dtos.medicalRecord;

import com.femcoders.pettrack.models.MedicalRecord;

public interface MedicalRecordMapper {
    MedicalRecordResponse entityToDto(MedicalRecord medicalRecord);
}
