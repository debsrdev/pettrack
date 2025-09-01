package com.femcoders.pettrack.services;

import com.femcoders.pettrack.dtos.medicalRecord.MedicalRecordMapper;
import com.femcoders.pettrack.dtos.medicalRecord.MedicalRecordResponse;
import com.femcoders.pettrack.models.MedicalRecord;
import com.femcoders.pettrack.models.Role;
import com.femcoders.pettrack.repositories.MedicalRecordRepository;
import com.femcoders.pettrack.security.UserDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalRecordServiceImpl implements MedicalRecordService {
    private final MedicalRecordMapper medicalRecordMapper;
    private final MedicalRecordRepository medicalRecordRepository;

    @Override
    public List<MedicalRecordResponse> GetAllMedicalRecords(UserDetail userDetail) {
        List<MedicalRecord> medicalRecords = medicalRecordRepository.findByPet_User_Id(userDetail.getId());

        if (userDetail.getRole().equals(Role.VETERINARY.name())) {
            medicalRecords = medicalRecordRepository.findAll();
        }

        return medicalRecords.stream()
                .map(medicalRecord -> medicalRecordMapper.entityToDto(medicalRecord))
                .toList();
    }
}
