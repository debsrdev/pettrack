package com.femcoders.pettrack.controllers;

import com.femcoders.pettrack.dtos.medicalRecord.MedicalRecordResponse;
import com.femcoders.pettrack.security.UserDetail;
import com.femcoders.pettrack.services.MedicalRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/medical-records")
public class MedicalRecordController {
    private final MedicalRecordService medicalRecordService;

    @GetMapping
    public ResponseEntity<List<MedicalRecordResponse>> getAllMedicalRecords(@AuthenticationPrincipal UserDetail userDetail) {
        List<MedicalRecordResponse> medicalRecords = medicalRecordService.GetAllMedicalRecords(userDetail);
        return ResponseEntity.ok(medicalRecords);
    }
}
