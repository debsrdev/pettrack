package com.femcoders.pettrack.repositories;

import com.femcoders.pettrack.models.MedicalRecord;
import com.femcoders.pettrack.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    List<MedicalRecord> findByPet_User_Id(Long userId);
    List<MedicalRecord> findByPetNameIgnoreCase(String name);
}
