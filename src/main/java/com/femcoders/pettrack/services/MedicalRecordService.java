package com.femcoders.pettrack.services;

import com.femcoders.pettrack.dtos.medicalRecord.MedicalRecordResponse;
import com.femcoders.pettrack.models.User;
import com.femcoders.pettrack.security.UserDetail;

import java.util.List;

public interface MedicalRecordService {
    List<MedicalRecordResponse> GetAllMedicalRecords(UserDetail userDetail);
    MedicalRecordResponse GetMedicalRecordById(Long id, UserDetail userDetail);
}
