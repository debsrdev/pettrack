package com.femcoders.pettrack.services;

import com.femcoders.pettrack.dtos.pet.PetMapperImpl;
import com.femcoders.pettrack.dtos.pet.PetResponse;
import com.femcoders.pettrack.models.Pet;
import com.femcoders.pettrack.repositories.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PetService {
    private final PetRepository petRepository;
    private final PetMapperImpl petMapperImpl;

    public List<PetResponse> getAllPets() {
        List<Pet> pets = petRepository.findAll();
        return pets.stream()
                .map(pet -> petMapperImpl.entityToDto(pet))
                .toList();
    }
}
