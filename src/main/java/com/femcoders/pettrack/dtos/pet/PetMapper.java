package com.femcoders.pettrack.dtos.pet;

import com.femcoders.pettrack.models.Pet;
import com.femcoders.pettrack.models.User;

public interface PetMapper {
    Pet dtoToEntity(PetRequest petRequest, User user);
    PetResponse entityToDto(Pet pet);
}
