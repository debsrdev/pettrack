package com.femcoders.pettrack.services;

import com.femcoders.pettrack.dtos.pet.PetRequest;
import com.femcoders.pettrack.dtos.pet.PetResponse;
import com.femcoders.pettrack.security.UserDetail;

import java.util.List;
import java.util.Map;

public interface PetService {
    List<PetResponse> getAllPets();
    List<PetResponse> getFilteredPets(String name, String species, String breed);
    PetResponse getPetById(Long id);
    PetResponse createPet(PetRequest petRequest, UserDetail userDetail);
    PetResponse updatePet(Long id, PetRequest petRequest, UserDetail userDetail);
    Map<String, String> deletePet(Long id, UserDetail userDetail);
}
