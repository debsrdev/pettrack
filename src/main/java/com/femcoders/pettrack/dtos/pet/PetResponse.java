package com.femcoders.pettrack.dtos.pet;

import java.time.LocalDate;

public record PetResponse(
        Long id,
        String name,
        String species,
        String breed,
        LocalDate birthDate,
        String image,
        String username
) {}
