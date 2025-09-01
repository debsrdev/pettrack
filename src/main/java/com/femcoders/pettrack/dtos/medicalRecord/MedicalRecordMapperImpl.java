package com.femcoders.pettrack.dtos.medicalRecord;

import com.femcoders.pettrack.models.MedicalRecord;
import com.femcoders.pettrack.models.Pet;
import com.femcoders.pettrack.models.User;
import org.springframework.stereotype.Component;

@Component
public class MedicalRecordMapperImpl implements MedicalRecordMapper {
    @Override
    public MedicalRecord dtoToEntity(MedicalRecordRequest medicalRecordRequest, Pet pet, User userVeterinary) {
        return MedicalRecord.builder()
                .description(medicalRecordRequest.description())
                .weight(medicalRecordRequest.weight())
                .date(medicalRecordRequest.date())
                .type(medicalRecordRequest.type())
                .pet(pet)
                .createdBy(userVeterinary)
                .build();
    }
    @Override
    public MedicalRecordResponse entityToDto(MedicalRecord medicalRecord) {
        return new MedicalRecordResponse(
                medicalRecord.getId(),
                medicalRecord.getDescription(),
                medicalRecord.getWeight(),
                medicalRecord.getDate(),
                medicalRecord.getType(),
                medicalRecord.getPet().getName(),
                medicalRecord.getCreatedBy().getUsername()
        );
    }
}
