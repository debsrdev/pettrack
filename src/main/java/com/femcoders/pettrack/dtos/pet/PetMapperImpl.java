package com.femcoders.pettrack.dtos.pet;

import com.femcoders.pettrack.models.Pet;
import org.springframework.stereotype.Component;

@Component
public class PetMapperImpl implements PetMapper {
    @Override
    public PetResponse entityToDto(Pet pet) {
        String username = (pet.getUser() != null) ? pet.getUser().getUsername() : null;
        return new PetResponse(
                pet.getId(),
                pet.getName(),
                pet.getSpecies(),
                pet.getBreed(),
                pet.getBirthDate(),
                pet.getImage(),
                username
        );
    }
}
