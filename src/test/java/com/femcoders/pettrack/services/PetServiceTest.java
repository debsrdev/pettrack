package com.femcoders.pettrack.services;

import com.femcoders.pettrack.dtos.pet.PetMapper;
import com.femcoders.pettrack.dtos.pet.PetResponse;
import com.femcoders.pettrack.models.Pet;
import com.femcoders.pettrack.models.User;
import com.femcoders.pettrack.repositories.PetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("PetService Unit Tests")
public class PetServiceTest {
    @Mock
    PetRepository petRepository;

    @Mock
    PetMapper petMapper;

    @InjectMocks
    PetService petService;

    private Pet pet1;
    private Pet pet2;
    private PetResponse petResponse1;
    private PetResponse petResponse2;

    @Nested
    @DisplayName("getAllPets()")
    class GetAllPetsTests {
        @BeforeEach
        void setup() {
            User user = User.builder()
                    .id(1L)
                    .username("Debora")
                    .build();

            pet1 = Pet.builder()
                    .id(1L)
                    .name("Luna")
                    .species("Perro")
                    .breed("Golden Retriever")
                    .birthDate(LocalDate.parse("2021-03-15"))
                    .image("https://example.com/images/luna.jpg")
                    .user(user)
                    .build();

            pet2 = Pet.builder()
                    .id(2L)
                    .name("Milo")
                    .species("Gato")
                    .breed("Siamés")
                    .birthDate(LocalDate.parse("2022-06-20"))
                    .image("https://example.com/images/Milo.jpg")
                    .user(user)
                    .build();

            petResponse1 = new PetResponse(
                    pet1.getId(),
                    pet1.getName(),
                    pet1.getSpecies(),
                    pet1.getBreed(),
                    pet1.getBirthDate(),
                    pet1.getImage(),
                    user.getUsername()
            );

            petResponse2 = new PetResponse(
                    pet2.getId(),
                    pet2.getName(),
                    pet2.getSpecies(),
                    pet2.getBreed(),
                    pet2.getBirthDate(),
                    pet2.getImage(),
                    user.getUsername()
            );

            given(petMapper.entityToDto(pet1)).willReturn(petResponse1);
            given(petMapper.entityToDto(pet2)).willReturn(petResponse2);
        }
        @Test
        @DisplayName("Should return a list of all pet responses")
        void shouldReturnListOfPetResponses(){
            given(petRepository.findAll()).willReturn(List.of(pet1, pet2));

            List<PetResponse> result = petService.getAllPets();

            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(petResponse1, petResponse2);

            verify(petRepository).findAll();
            verify(petMapper).entityToDto(pet1);
            verify(petMapper).entityToDto(pet2);
        }
    }
}
