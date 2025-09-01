package com.femcoders.pettrack.services;

import com.femcoders.pettrack.dtos.medicalRecord.MedicalRecordMapper;
import com.femcoders.pettrack.dtos.medicalRecord.MedicalRecordRequest;
import com.femcoders.pettrack.dtos.medicalRecord.MedicalRecordResponse;
import com.femcoders.pettrack.exceptions.EntityNotFoundException;
import com.femcoders.pettrack.models.*;
import com.femcoders.pettrack.repositories.MedicalRecordRepository;
import com.femcoders.pettrack.repositories.PetRepository;
import com.femcoders.pettrack.repositories.UserRepository;
import com.femcoders.pettrack.security.UserDetail;
import com.femcoders.pettrack.utils.RoleValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class MedicalRecordServiceImpl implements MedicalRecordService {
    private final MedicalRecordMapper medicalRecordMapper;
    private final MedicalRecordRepository medicalRecordRepository;
    private final PetRepository petRepository;
    private final UserRepository userRepository;

    public List<MedicalRecordResponse> getAllMedicalRecords(UserDetail userDetail) {
        List<MedicalRecord> medicalRecords = medicalRecordRepository.findByPet_User_Id(userDetail.getId());

        if (userDetail.getRole().equals(Role.VETERINARY.name())) {
            medicalRecords = medicalRecordRepository.findAll();
        }

        return medicalRecords.stream()
                .map(medicalRecord -> medicalRecordMapper.entityToDto(medicalRecord))
                .toList();
    }

    public MedicalRecordResponse getMedicalRecordById(Long id, UserDetail userDetail) {
        MedicalRecord medicalRecord = medicalRecordRepository.findById(id)
                .orElseThrow(()->new EntityNotFoundException(MedicalRecord.class.getSimpleName(), id));

        if (RoleValidator.isVeterinary(userDetail)) {
            return medicalRecordMapper.entityToDto(medicalRecord);
        }

        Long loggedUserId = userDetail.getId();
        Long ownerId = medicalRecord.getPet().getUser().getId();

        if (!loggedUserId.equals(ownerId)) {
            throw new SecurityException("You do not have permission to view this medical record");
        }

        return medicalRecordMapper.entityToDto(medicalRecord);
    }

    public List<MedicalRecordResponse> getMedicalRecordsByPetName(String petName, UserDetail userDetail) {
        List<MedicalRecord> medicalRecords = medicalRecordRepository.findByPetNameIgnoreCase(petName);

        if (RoleValidator.isVeterinary(userDetail)) {
            return medicalRecords.stream()
                    .map(medicalRecord -> medicalRecordMapper.entityToDto(medicalRecord))
                    .toList();
        }
        if (medicalRecords.isEmpty() || !medicalRecords.getFirst().getPet().getUser().getId().equals(userDetail.getId())) {
            throw new SecurityException("You do not have permission to view this medical record");
        }

        return medicalRecords.stream()
                .map(medicalRecord -> medicalRecordMapper.entityToDto(medicalRecord))
                .toList();
    }

    @Transactional
    public MedicalRecordResponse createMedicalRecord(MedicalRecordRequest medicalRecordRequest, UserDetail userDetail) {
        RoleValidator.validateVeterinary(userDetail, "Only veterinaries can manage medical records");

        Pet pet = petRepository.findById(medicalRecordRequest.petId())
                .orElseThrow(()->new EntityNotFoundException(Pet.class.getSimpleName(), medicalRecordRequest.petId()));

        User userVeterinary = userRepository.findById(userDetail.getId())
                .orElseThrow(()->new EntityNotFoundException(User.class.getSimpleName(), userDetail.getId()));

        MedicalRecord medicalRecord = medicalRecordMapper.dtoToEntity(medicalRecordRequest, pet, userVeterinary);
        MedicalRecord medicalRecordSaved = medicalRecordRepository.save(medicalRecord);

        return medicalRecordMapper.entityToDto(medicalRecordSaved);
    }

    @Transactional
    public MedicalRecordResponse updateMedicalRecord(Long id, MedicalRecordRequest medicalRecordRequest, UserDetail userDetail) {
        RoleValidator.validateVeterinary(userDetail, "Only veterinaries can manage medical records");

        MedicalRecord medicalRecordToUpdate = medicalRecordRepository.findById(id)
                .orElseThrow(() ->new EntityNotFoundException(MedicalRecord.class.getSimpleName(), id));

        medicalRecordToUpdate.setDescription(medicalRecordRequest.description());
        medicalRecordToUpdate.setWeight(medicalRecordRequest.weight());
        medicalRecordToUpdate.setDate(medicalRecordRequest.date());
        medicalRecordToUpdate.setType(medicalRecordRequest.type());

        Pet newPet = petRepository.findById(medicalRecordRequest.petId())
                .orElseThrow(()->new NoSuchElementException("Pet not found with id " + medicalRecordRequest.petId()));

        medicalRecordToUpdate.setPet(newPet);

        medicalRecordRepository.save(medicalRecordToUpdate);
        return medicalRecordMapper.entityToDto(medicalRecordToUpdate);
    }

        @Transactional
        public Map<String, String> deleteMedicalRecord(Long id, UserDetail userDetail) {
            RoleValidator.validateVeterinary(userDetail, "Only veterinaries can manage medical records");

            MedicalRecord medicalRecordToDelete = medicalRecordRepository.findById(id)
                    .orElseThrow(()->new EntityNotFoundException(MedicalRecord.class.getSimpleName(), id));

            medicalRecordRepository.delete(medicalRecordToDelete);

            String messsage = "Medical record with id: " + medicalRecordToDelete.getId() + " from pet " + medicalRecordToDelete.getPet().getName() + " has been deleted successfully";
            return Map.of("message", messsage);
        }
}
