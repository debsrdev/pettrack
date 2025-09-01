package com.femcoders.pettrack.dtos.medicalRecord;

import com.femcoders.pettrack.models.MedicalRecord;
import org.springframework.stereotype.Component;

@Component
public class MedicalRecordMapperImpl implements MedicalRecordMapper {
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
