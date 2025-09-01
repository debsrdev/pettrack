package com.femcoders.pettrack.repositories;

import com.femcoders.pettrack.models.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PetRepository extends JpaRepository<Pet, Long>, JpaSpecificationExecutor<Pet> {
}
