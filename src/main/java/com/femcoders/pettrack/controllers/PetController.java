package com.femcoders.pettrack.controllers;

import com.femcoders.pettrack.dtos.pet.PetRequest;
import com.femcoders.pettrack.dtos.pet.PetResponse;
import com.femcoders.pettrack.services.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
