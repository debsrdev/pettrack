package com.femcoders.pettrack.dtos.pet;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record PetRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 20, message = "Name must be less than 20 characters")
        String name,

        @NotBlank(message = "Species is required")
        @Size(max = 50, message = "Species must be less than 50 characters")
        String species,

        @NotBlank(message = "Breed is required")
        @Size(max = 20, message = "Breed must be less than 20 characters")
        String breed,

        @NotNull(message = "Birth date is required")
        @PastOrPresent(message = "Birth date must be in the past or today")
        LocalDate birthDate,

        @NotBlank(message = "Image is required")
        @Pattern(message = "Must be a valid URL", regexp = "^(http|https)://.*$")
        String image,

        @NotBlank(message = "Username is required")
        @Size(max = 50, message = "Username must be less than 50 characters")
        String username
) {}
