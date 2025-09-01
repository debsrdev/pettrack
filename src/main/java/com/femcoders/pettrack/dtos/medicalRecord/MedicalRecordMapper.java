package com.femcoders.pettrack.dtos.medicalRecord;

import com.femcoders.pettrack.models.MedicalRecord;
import com.femcoders.pettrack.models.Pet;
import com.femcoders.pettrack.models.User;

public interface MedicalRecordMapper {
    MedicalRecord dtoToEntity(MedicalRecordRequest medicalRecordRequest, Pet pet, User userVeterinary);
    MedicalRecordResponse entityToDto(MedicalRecord medicalRecord);
}
