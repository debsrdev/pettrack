package com.femcoders.pettrack.services;

import com.femcoders.pettrack.dtos.medicalRecord.MedicalRecordRequest;
import com.femcoders.pettrack.dtos.medicalRecord.MedicalRecordResponse;
import com.femcoders.pettrack.models.Pet;
import com.femcoders.pettrack.models.User;
import com.femcoders.pettrack.security.UserDetail;

import java.util.List;

public interface MedicalRecordService {
    List<MedicalRecordResponse> getAllMedicalRecords(UserDetail userDetail);
    MedicalRecordResponse getMedicalRecordById(Long id, UserDetail userDetail);
    List<MedicalRecordResponse> getMedicalRecordsByPetName(String petName, UserDetail userDetail);
    MedicalRecordResponse createMedicalRecord(MedicalRecordRequest medicalRecordRequest, UserDetail userDetail);
}
