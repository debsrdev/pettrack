package com.femcoders.pettrack.services;

import com.femcoders.pettrack.dtos.medicalRecord.MedicalRecordMapper;
import com.femcoders.pettrack.dtos.medicalRecord.MedicalRecordResponse;
import com.femcoders.pettrack.exceptions.EntityNotFoundException;
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

    @Override
    public MedicalRecordResponse GetMedicalRecordById(Long id, UserDetail userDetail) {
        MedicalRecord medicalRecord = medicalRecordRepository.findById(id)
                .orElseThrow(()->new EntityNotFoundException(MedicalRecord.class.getSimpleName(), id));

        if (userDetail.getRole().equals(Role.VETERINARY.name())) {
            return medicalRecordMapper.entityToDto(medicalRecord);
        }

        Long loggedUserId = userDetail.getId();
        Long ownerId = medicalRecord.getPet().getUser().getId();

        if (!loggedUserId.equals(ownerId)) {
            throw new SecurityException("You do not have permission to view this medical record");
        }

        return medicalRecordMapper.entityToDto(medicalRecord);
    }
}
