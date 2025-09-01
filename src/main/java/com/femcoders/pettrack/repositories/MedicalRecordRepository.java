package com.femcoders.pettrack.repositories;

import com.femcoders.pettrack.models.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    List<MedicalRecord> findByPet_User_Id(Long userId);
}
