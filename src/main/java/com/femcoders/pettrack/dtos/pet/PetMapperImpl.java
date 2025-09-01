package com.femcoders.pettrack.dtos.pet;

import com.femcoders.pettrack.models.Pet;
import com.femcoders.pettrack.models.User;
import org.springframework.stereotype.Component;

@Component
public class PetMapperImpl implements PetMapper {
    @Override
    public Pet dtoToEntity(PetRequest petRequest, User user) {
        return Pet.builder()
                .name(petRequest.name())
                .species(petRequest.species())
                .breed(petRequest.breed())
                .birthDate(petRequest.birthDate())
                .image(petRequest.image())
                .user(user)
                .build();
    }

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
