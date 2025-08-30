package com.femcoders.pettrack.controllers;

import com.femcoders.pettrack.dtos.pet.PetRequest;
import com.femcoders.pettrack.dtos.pet.PetResponse;
import com.femcoders.pettrack.security.UserDetail;
import com.femcoders.pettrack.services.PetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pets")
public class PetController {
    private final PetService petService;

    @GetMapping
    public ResponseEntity<List<PetResponse>> getAllPets() {
        List<PetResponse> pets = petService.getAllPets();
        return ResponseEntity.ok(pets);
    }

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

    @GetMapping("/{id}")
    public ResponseEntity<PetResponse> getPetById(@PathVariable Long id) {
        PetResponse pet = petService.getPetById(id);
        return ResponseEntity.ok(pet);
    }

    @PostMapping
    public ResponseEntity<PetResponse> createPet(@RequestBody @Valid PetRequest petRequest, @AuthenticationPrincipal UserDetail userDetail) {
        PetResponse createdPet = petService.createPet(petRequest, userDetail);
        return new ResponseEntity<>(createdPet, HttpStatus.CREATED);
    }
}
