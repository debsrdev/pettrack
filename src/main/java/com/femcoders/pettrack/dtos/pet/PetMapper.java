package com.femcoders.pettrack.dtos.pet;

import com.femcoders.pettrack.models.Pet;

public interface PetMapper {
    PetResponse entityToDto(Pet pet);
}
