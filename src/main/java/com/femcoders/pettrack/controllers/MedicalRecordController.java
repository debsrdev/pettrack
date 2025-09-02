package com.femcoders.pettrack.controllers;

import com.femcoders.pettrack.dtos.medicalRecord.MedicalRecordRequest;
import com.femcoders.pettrack.dtos.medicalRecord.MedicalRecordResponse;
import com.femcoders.pettrack.dtos.pet.PetRequest;
import com.femcoders.pettrack.dtos.pet.PetResponse;
import com.femcoders.pettrack.security.UserDetail;
import com.femcoders.pettrack.services.MedicalRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/medical-records")
@Tag(name = "Medical Records", description = "Endpoints for managing pets' medical records")
@SecurityRequirement(name = "bearerAuth")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    @Operation(
            summary = "Get all medical records",
            description = "Returns all medical records visible for the authenticated user. " +
                    "In PetTrack, access is restricted and validated via role; typically only VETERINARY can see all records."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Medical records retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = MedicalRecordResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized (missing/invalid JWT)"),
            @ApiResponse(responseCode = "403", description = "Forbidden (insufficient permissions)")
    })
    @GetMapping
    public ResponseEntity<List<MedicalRecordResponse>> getAllMedicalRecords(@AuthenticationPrincipal UserDetail userDetail) {
        List<MedicalRecordResponse> medicalRecords = medicalRecordService.getAllMedicalRecords(userDetail);
        return ResponseEntity.ok(medicalRecords);
    }

    @Operation(
            summary = "Get medical record by ID",
            description = "Returns a single medical record by its ID, if the authenticated user has permission."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Medical record retrieved successfully",
                    content = @Content(schema = @Schema(implementation = MedicalRecordResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized (missing/invalid JWT)"),
            @ApiResponse(responseCode = "403", description = "Forbidden (insufficient permissions)"),
            @ApiResponse(responseCode = "404", description = "Medical record not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MedicalRecordResponse> getMedicalRecordById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetail userDetail) {
        MedicalRecordResponse medicalRecord = medicalRecordService.getMedicalRecordById(id, userDetail);
        return ResponseEntity.ok(medicalRecord);
    }

    @Operation(
            summary = "Get medical records by pet name",
            description = "Returns all medical records for a given pet name, if the authenticated user has permission."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Medical records retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = MedicalRecordResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized (missing/invalid JWT)"),
            @ApiResponse(responseCode = "403", description = "Forbidden (insufficient permissions)"),
            @ApiResponse(responseCode = "404", description = "Pet not found or no records available")
    })
    @GetMapping("/pet/{petName}")
    public ResponseEntity<List<MedicalRecordResponse>> getMedicalRecordsByPetName(
            @PathVariable String petName,
            @AuthenticationPrincipal UserDetail userDetail) {
        List<MedicalRecordResponse> medicalRecord = medicalRecordService.getMedicalRecordsByPetName(petName, userDetail);
        return ResponseEntity.ok(medicalRecord);
    }

    @Operation(
            summary = "Create medical record",
            description = "Creates a new medical record for a pet. " +
                    "Only users with the VETERINARY role can perform this operation."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Medical record created successfully",
                    content = @Content(schema = @Schema(implementation = MedicalRecordResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error in request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized (missing/invalid JWT)"),
            @ApiResponse(responseCode = "403", description = "Forbidden (insufficient role)"),
            @ApiResponse(responseCode = "404", description = "Pet or user not found")
    })
    @PostMapping
    public ResponseEntity<MedicalRecordResponse> createMedicalRecord(
            @RequestBody @Valid MedicalRecordRequest medicalRecordRequest,
            @AuthenticationPrincipal UserDetail userDetail
    ) {
        MedicalRecordResponse createdMedicalRecord = medicalRecordService.createMedicalRecord(medicalRecordRequest, userDetail);
        return new ResponseEntity<>(createdMedicalRecord, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Update medical record",
            description = "Updates an existing medical record by ID. " +
                    "Only users with the VETERINARY role can perform this operation."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Medical record updated successfully",
                    content = @Content(schema = @Schema(implementation = MedicalRecordResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error in request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized (missing/invalid JWT)"),
            @ApiResponse(responseCode = "403", description = "Forbidden (insufficient role)"),
            @ApiResponse(responseCode = "404", description = "Medical record or pet not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<MedicalRecordResponse> updateMedicalRecord(@PathVariable Long id,
                                                 @RequestBody @Valid MedicalRecordRequest medicalRecordRequest,
                                                 @AuthenticationPrincipal UserDetail userDetail) {
        MedicalRecordResponse updatedMedicalRecord = medicalRecordService.updateMedicalRecord(id, medicalRecordRequest, userDetail);
        return ResponseEntity.ok(updatedMedicalRecord);
    }

    @Operation(
            summary = "Delete medical record",
            description = "Deletes a medical record by its ID and returns a confirmation message. " +
                    "Only users with the VETERINARY role can perform this operation."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Medical record deleted successfully",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized (missing/invalid JWT)"),
            @ApiResponse(responseCode = "403", description = "Forbidden (insufficient role)"),
            @ApiResponse(responseCode = "404", description = "Medical record not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteMedicalRecord(@PathVariable Long id,
                                                         @AuthenticationPrincipal UserDetail userDetail) {
        Map<String, String> response = medicalRecordService.deleteMedicalRecord(id, userDetail);
        return ResponseEntity.ok(response);
    }
}
