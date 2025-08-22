package com.femcoders.pettrack.services;

import com.femcoders.pettrack.dtos.pet.PetMapper;
import com.femcoders.pettrack.dtos.pet.PetResponse;
import com.femcoders.pettrack.models.Pet;
import com.femcoders.pettrack.repositories.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

@Service
@RequiredArgsConstructor
public class PetService {
    private final PetRepository petRepository;
    private final PetMapper petMapper;

    public List<PetResponse> getAllPets() {
        List<Pet> pets = petRepository.findAll();
        return pets.stream()
                .map(pet -> petMapper.entityToDto(pet))
                .toList();
    }

    public List<PetResponse> getFilteredPets(String name, String species, String breed) {
        List<Pet> pets = petRepository.findAll(
                (root, query, cb) -> {
                    List<Predicate> predicates = new ArrayList<>();
                    if (name != null && !name.isBlank()) {
                        predicates.add(cb.like(cb.lower(
                                root.get("name")),
                                "%" + name.trim().toLowerCase() + "%"));
                    }
                    if (species != null && !species.isBlank()) {
                        predicates.add(cb.like(cb.lower(
                                root.get("species")),
                                "%" + species.trim().toLowerCase() + "%"));
                    }
                    if (breed != null && !breed.isBlank()) {
                        predicates.add(cb.like(cb.lower(
                                root.get("breed")),
                                "%" + breed.trim().toLowerCase() + "%"));
                    }
                    return cb.and(predicates.toArray(predicates.toArray(new Predicate[0])));
                });
        return pets.stream()
                .map(pet -> petMapper.entityToDto(pet))
                .toList();
    }
}
