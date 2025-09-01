package com.femcoders.pettrack.controllers;

import com.femcoders.pettrack.dtos.medicalRecord.MedicalRecordRequest;
import com.femcoders.pettrack.dtos.medicalRecord.MedicalRecordResponse;
import com.femcoders.pettrack.dtos.pet.PetRequest;
import com.femcoders.pettrack.dtos.pet.PetResponse;
import com.femcoders.pettrack.security.UserDetail;
import com.femcoders.pettrack.services.MedicalRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/medical-records")
public class MedicalRecordController {
    private final MedicalRecordService medicalRecordService;

    @GetMapping
    public ResponseEntity<List<MedicalRecordResponse>> getAllMedicalRecords(@AuthenticationPrincipal UserDetail userDetail) {
        List<MedicalRecordResponse> medicalRecords = medicalRecordService.getAllMedicalRecords(userDetail);
        return ResponseEntity.ok(medicalRecords);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicalRecordResponse> getMedicalRecordById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetail userDetail) {
        MedicalRecordResponse medicalRecord = medicalRecordService.getMedicalRecordById(id, userDetail);
        return ResponseEntity.ok(medicalRecord);
    }

    @GetMapping("/pet/{petName}")
    public ResponseEntity<List<MedicalRecordResponse>> getMedicalRecordsByPetName(
            @PathVariable String petName,
            @AuthenticationPrincipal UserDetail userDetail) {
        List<MedicalRecordResponse> medicalRecord = medicalRecordService.getMedicalRecordsByPetName(petName, userDetail);
        return ResponseEntity.ok(medicalRecord);
    }

    @PostMapping
    public ResponseEntity<MedicalRecordResponse> createMedicalRecord(
            @RequestBody @Valid MedicalRecordRequest medicalRecordRequest,
            @AuthenticationPrincipal UserDetail userDetail
    ) {
        MedicalRecordResponse createdMedicalRecord = medicalRecordService.createMedicalRecord(medicalRecordRequest, userDetail);
        return new ResponseEntity<>(createdMedicalRecord, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicalRecordResponse> updateMedicalRecord(@PathVariable Long id,
                                                 @RequestBody @Valid MedicalRecordRequest medicalRecordRequest,
                                                 @AuthenticationPrincipal UserDetail userDetail) {
        MedicalRecordResponse updatedMedicalRecord = medicalRecordService.updateMedicalRecord(id, medicalRecordRequest, userDetail);
        return ResponseEntity.ok(updatedMedicalRecord);
    }
}
