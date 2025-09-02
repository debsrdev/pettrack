package com.femcoders.pettrack.services;

import com.femcoders.pettrack.dtos.pet.PetMapper;
import com.femcoders.pettrack.dtos.pet.PetRequest;
import com.femcoders.pettrack.dtos.pet.PetResponse;
import com.femcoders.pettrack.exceptions.EntityNotFoundException;
import com.femcoders.pettrack.models.Pet;
import com.femcoders.pettrack.models.User;
import com.femcoders.pettrack.repositories.PetRepository;
import com.femcoders.pettrack.repositories.UserRepository;
import com.femcoders.pettrack.security.UserDetail;
import com.femcoders.pettrack.utils.RoleValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import jakarta.persistence.criteria.Predicate;

@Service
@RequiredArgsConstructor
public class PetService {
    private final PetRepository petRepository;
    private final PetMapper petMapper;
    private final UserRepository userRepository;

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

                    boolean hasName = name != null && !name.isBlank();
                    boolean hasSpecies = species != null && !species.isBlank();
                    boolean hasBreed = breed != null && !breed.isBlank();

                    if (hasName) {
                        predicates.add(cb.like(cb.lower(
                                root.get("name")),
                                "%" + name.trim().toLowerCase() + "%"));
                    }
                    if (hasSpecies) {
                        predicates.add(cb.like(cb.lower(
                                root.get("species")),
                                "%" + species.trim().toLowerCase() + "%"));
                    }
                    if (hasBreed) {
                        predicates.add(cb.like(cb.lower(
                                root.get("breed")),
                                "%" + breed.trim().toLowerCase() + "%"));
                    }
                    return cb.and(predicates.toArray(new Predicate[0]));
                });
        return pets.stream()
                .map(pet -> petMapper.entityToDto(pet))
                .toList();
    }

    public PetResponse getPetById(Long id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(()->new EntityNotFoundException(Pet.class.getSimpleName(), id));
        return petMapper.entityToDto(pet);
    }

    @Transactional
    public PetResponse createPet(PetRequest petRequest, UserDetail userDetail) {
        RoleValidator.validateVeterinary(userDetail, "Only veterinaries can manage pets");

        User petOwner = userRepository.findByUsernameIgnoreCase(petRequest.username())
                .orElseThrow(()->new NoSuchElementException("User not found with username " + petRequest.username()));

        Pet pet = petMapper.dtoToEntity(petRequest, petOwner);
        petRepository.save(pet);

        return petMapper.entityToDto(pet);
    }

    @Transactional
    public PetResponse updatePet(Long id, PetRequest petRequest, UserDetail userDetail) {
        RoleValidator.validateVeterinary(userDetail, "Only veterinaries can manage pets");

        Pet petToUpdate = petRepository.findById(id)
                .orElseThrow(()->new EntityNotFoundException(Pet.class.getSimpleName(), id));

        petToUpdate.setName(petRequest.name());
        petToUpdate.setSpecies(petRequest.species());
        petToUpdate.setBreed(petRequest.breed());
        petToUpdate.setBirthDate(petRequest.birthDate());
        petToUpdate.setImage(petRequest.image());

        User newPetOwner = userRepository.findByUsernameIgnoreCase(petRequest.username())
                .orElseThrow(()->new NoSuchElementException("User not found with username " + petRequest.username()));

        petToUpdate.setUser(newPetOwner);

        petRepository.save(petToUpdate);
        return petMapper.entityToDto(petToUpdate);
    }

    @Transactional
    public Map<String, String> deletePet(Long id, UserDetail userDetail) {
        RoleValidator.validateVeterinary(userDetail, "Only veterinaries can manage pets");

        Pet petToDelete = petRepository.findById(id)
                .orElseThrow(()->new EntityNotFoundException(Pet.class.getSimpleName(), id));

        petRepository.delete(petToDelete);

        String message = "Pet '" + petToDelete.getName() + "' with id:" + petToDelete.getId() + " has been deleted successfully";
        return Map.of("message", message);
    }
}
