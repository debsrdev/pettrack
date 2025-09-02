package com.femcoders.pettrack.controllers;

import com.femcoders.pettrack.dtos.pet.PetRequest;
import com.femcoders.pettrack.dtos.pet.PetResponse;
import com.femcoders.pettrack.security.UserDetail;
import com.femcoders.pettrack.services.PetServiceImpl;
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
@Tag(name = "Pets", description = "Endpoints for managing pets")
@RequestMapping("/api/pets")
public class PetController {
    private final PetServiceImpl petService;

    @Operation(
            summary = "Get all pets",
            description = "Returns a list of all registered pets."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pets retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PetResponse.class))))
    })
    @GetMapping
    public ResponseEntity<List<PetResponse>> getAllPets() {
        List<PetResponse> pets = petService.getAllPets();
        return ResponseEntity.ok(pets);
    }

    @Operation(
            summary = "Filter pets by name, species or breed",
            description = "Returns pets filtered by optional parameters: name, species or breed."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Filtered pets retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PetResponse.class))))
    })
    @GetMapping("/filter")
    public ResponseEntity<List<PetResponse>> getFilteredPets(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String species,
            @RequestParam(required = false) String breed
    ) {
        return ResponseEntity.ok(
            petService.getFilteredPets(name, species, breed)
        );
    }

    @Operation(
            summary = "Get pet by ID",
            description = "Returns a single pet by its ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pet retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PetResponse.class))),
            @ApiResponse(responseCode = "404", description = "Pet not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PetResponse> getPetById(@PathVariable Long id) {
        PetResponse pet = petService.getPetById(id);
        return ResponseEntity.ok(pet);
    }

    @Operation(
            summary = "Create new pet",
            description = "Creates a new pet and assigns it to an existing user. Only users with the VETERINARY role can perform this operation."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pet created successfully",
                    content = @Content(schema = @Schema(implementation = PetResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error in request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized (missing/invalid JWT)"),
            @ApiResponse(responseCode = "403", description = "Forbidden (insufficient role)"),
            @ApiResponse(responseCode = "404", description = "User not found (owner of the pet)")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<PetResponse> createPet(@RequestBody @Valid PetRequest petRequest,
                                                 @AuthenticationPrincipal UserDetail userDetail) {
        PetResponse createdPet = petService.createPet(petRequest, userDetail);
        return new ResponseEntity<>(createdPet, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Update pet",
            description = "Updates the details of a pet by its ID. Only users with the VETERINARY role can perform this operation."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pet updated successfully",
                    content = @Content(schema = @Schema(implementation = PetResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error in request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized (missing/invalid JWT)"),
            @ApiResponse(responseCode = "403", description = "Forbidden (insufficient role)"),
            @ApiResponse(responseCode = "404", description = "Pet not found or user not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    public ResponseEntity<PetResponse> updatePet(@PathVariable Long id,
                                                 @RequestBody @Valid PetRequest petRequest,
                                                 @AuthenticationPrincipal UserDetail userDetail) {
        PetResponse updatedPet = petService.updatePet(id, petRequest, userDetail);
        return ResponseEntity.ok(updatedPet);
    }

    @Operation(
            summary = "Delete pet",
            description = "Deletes a pet by its ID and returns a confirmation message. Only users with the VETERINARY role can perform this operation."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pet deleted successfully",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized (missing/invalid JWT)"),
            @ApiResponse(responseCode = "403", description = "Forbidden (insufficient role)"),
            @ApiResponse(responseCode = "404", description = "Pet not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deletePet(@PathVariable Long id,
                                                         @AuthenticationPrincipal UserDetail userDetail) {
        Map<String, String> response = petService.deletePet(id, userDetail);
        return ResponseEntity.ok(response);
    }
}
