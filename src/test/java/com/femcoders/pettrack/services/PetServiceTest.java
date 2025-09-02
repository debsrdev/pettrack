package com.femcoders.pettrack.services;

import com.femcoders.pettrack.dtos.pet.PetMapper;
import com.femcoders.pettrack.dtos.pet.PetRequest;
import com.femcoders.pettrack.dtos.pet.PetResponse;
import com.femcoders.pettrack.exceptions.EntityNotFoundException;
import com.femcoders.pettrack.models.Pet;
import com.femcoders.pettrack.models.Role;
import com.femcoders.pettrack.models.User;
import com.femcoders.pettrack.repositories.PetRepository;
import com.femcoders.pettrack.repositories.UserRepository;
import com.femcoders.pettrack.security.UserDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("PetService Unit Tests")
public class PetServiceTest {
    @Mock
    PetRepository petRepository;

    @Mock
    PetMapper petMapper;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    PetServiceImpl petService;

    private Pet pet1;
    private Pet pet2;
    private PetResponse petResponse1;
    private PetResponse petResponse2;
    private Pet petNew;
    private PetRequest petRequestNew;
    private PetResponse petResponseNew;
    private PetRequest updatedPetRequest;
    private PetRequest updatedPetRequestUsernameInvalid;
    private User user;
    private UserDetail userVeterinary;
    private UserDetail userRegular;

    @BeforeEach
    void setup() {
        user = User.builder()
                .id(1L)
                .username("Debora")
                .build();

        userVeterinary = new UserDetail(
                User.builder()
                        .id(4L)
                        .username("Carmen")
                        .role(Role.VETERINARY)
                        .build()
        );

        userRegular = new UserDetail(
                User.builder()
                        .id(20L)
                        .username("User test")
                        .role(Role.USER)
                        .build()
        );

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

        lenient().when(petMapper.entityToDto(pet1)).thenReturn(petResponse1);
        lenient().when(petMapper.entityToDto(pet2)).thenReturn(petResponse2);

        petRequestNew = new PetRequest(
                "Trufa",
                "Perro",
                "Caniche Toy",
                LocalDate.parse("2021-03-15"),
                "https://example.com/images/trufa.jpg",
                "Debora"
        );

        petNew = Pet.builder()
                .id(20L)
                .name("Trufa")
                .species("Perro")
                .breed("Caniche Toy")
                .birthDate(LocalDate.parse("2021-03-15"))
                .image("https://example.com/images/trufa.jpg")
                .user(user)
                .build();

        petResponseNew = new PetResponse(
                20L,
                "Trufa",
                "Perro",
                "Caniche Toy",
                LocalDate.parse("2021-03-15"),
                "https://example.com/images/trufa.jpg",
                "Debora"
        );

        updatedPetRequest = new PetRequest(
                "Luna",
                "Perro",
                "Golden Retriever",
                LocalDate.parse("2021-03-15"),
                "https://example.com/images/luna.jpg",
                "Debora"
        );

        updatedPetRequestUsernameInvalid = new PetRequest(
                "Luna",
                "Perro",
                "Golden Retriever",
                LocalDate.parse("2021-03-15"),
                "https://example.com/images/luna.jpg",
                "Nombre de usuario"
        );
    }

    @Nested
    @DisplayName("getAllPets()")
    class GetAllPetsTests {
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

    @Nested
    @DisplayName("getFilteredPets()")
    class GetFilteredPetsTests {
        @Test
        @DisplayName("Should return filtered pets by name")
        void shouldReturnListOfPetsFilteredByName() {
            given(petRepository.findAll(any(Specification.class))).willReturn(List.of(pet1));
            given(petMapper.entityToDto(pet1)).willReturn(petResponse1);

            List<PetResponse> result = petService.getFilteredPets("Luna", null, null);

            assertThat(result).hasSizeGreaterThanOrEqualTo(1);
            assertThat(result.getFirst().name()).containsIgnoringCase("luna");

            verify(petRepository).findAll(any(Specification.class));
            verify(petMapper).entityToDto(pet1);
        }

        @Test
        @DisplayName("Should return filtered pets by name and species")
        void shouldReturnListOfPetsFilteredByNameAndSpecies() {
            given(petRepository.findAll(any(Specification.class))).willReturn(List.of(pet1));
            given(petMapper.entityToDto(pet1)).willReturn(petResponse1);

            List<PetResponse> result = petService.getFilteredPets("Luna", "perro", null);

            assertThat(result).hasSizeGreaterThanOrEqualTo(1);
            assertThat(result.getFirst().name()).containsIgnoringCase("luna");
            assertThat(result.getFirst().species()).containsIgnoringCase("perro");

            verify(petRepository).findAll(any(Specification.class));
            verify(petMapper).entityToDto(pet1);
        }

        @Test
        @DisplayName("Should return all list unfiltered when name is blank")
        void shouldReturnAllListOfPetsUnfilteredWhenNameIsBlank() {
            given(petRepository.findAll(any(Specification.class))).willReturn(List.of(pet1, pet2));
            given(petMapper.entityToDto(pet1)).willReturn(petResponse1);
            given(petMapper.entityToDto(pet2)).willReturn(petResponse2);

            List<PetResponse> result = petService.getFilteredPets(" ", null, null);

            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(petResponse1, petResponse2);

            verify(petRepository).findAll(any(Specification.class));
            verify(petMapper).entityToDto(pet1);
            verify(petMapper).entityToDto(pet2);
        }

        @Test
        @DisplayName("Should return all list unfiltered when name is empty")
        void shouldReturnAllListOfPetsUnfilteredWhenNameIsEmpty() {
            given(petRepository.findAll(any(Specification.class))).willReturn(List.of(pet1, pet2));
            given(petMapper.entityToDto(pet1)).willReturn(petResponse1);
            given(petMapper.entityToDto(pet2)).willReturn(petResponse2);

            List<PetResponse> result = petService.getFilteredPets("", null, null);

            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(petResponse1, petResponse2);

            verify(petRepository).findAll(any(Specification.class));
            verify(petMapper).entityToDto(pet1);
            verify(petMapper).entityToDto(pet2);
        }

        @Test
        @DisplayName("Should return filtered pets by species")
        void shouldReturnListOfPetsFilteredBySpecies() {
            given(petRepository.findAll(any(Specification.class))).willReturn(List.of(pet2));
            given(petMapper.entityToDto(pet2)).willReturn(petResponse2);

            List<PetResponse> result = petService.getFilteredPets(null, "Gato", null);

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().species()).containsIgnoringCase("gato");

            verify(petRepository).findAll(any(Specification.class));
            verify(petMapper).entityToDto(pet2);
        }

        @Test
        @DisplayName("Should return all list unfiltered when species is blank")
        void shouldReturnAllListOfPetsUnfilteredWhenSpeciesIsBlank() {
            given(petRepository.findAll(any(Specification.class))).willReturn(List.of(pet1, pet2));
            given(petMapper.entityToDto(pet1)).willReturn(petResponse1);
            given(petMapper.entityToDto(pet2)).willReturn(petResponse2);

            List<PetResponse> result = petService.getFilteredPets(null, " ", null);

            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(petResponse1, petResponse2);

            verify(petRepository).findAll(any(Specification.class));
            verify(petMapper).entityToDto(pet1);
            verify(petMapper).entityToDto(pet2);
        }

        @Test
        @DisplayName("Should return all list unfiltered when species is empty")
        void shouldReturnAllListOfPetsUnfilteredWhenSpeciesIsEmpty() {
            given(petRepository.findAll(any(Specification.class))).willReturn(List.of(pet1, pet2));
            given(petMapper.entityToDto(pet1)).willReturn(petResponse1);
            given(petMapper.entityToDto(pet2)).willReturn(petResponse2);

            List<PetResponse> result = petService.getFilteredPets(null, "", null);

            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(petResponse1, petResponse2);

            verify(petRepository).findAll(any(Specification.class));
            verify(petMapper).entityToDto(pet1);
            verify(petMapper).entityToDto(pet2);
        }

        @Test
        @DisplayName("Should return filtered pets by breed")
        void shouldReturnListOfPetsFilteredByBreed() {
            given(petRepository.findAll(any(Specification.class))).willReturn(List.of(pet1));
            given(petMapper.entityToDto(pet1)).willReturn(petResponse1);

            List<PetResponse> result = petService.getFilteredPets(null, null, "Golden");

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().breed()).containsIgnoringCase("golden");

            verify(petRepository).findAll(any(Specification.class));
            verify(petMapper).entityToDto(pet1);
        }

        @Test
        @DisplayName("Should return all list unfiltered when breed is blank")
        void shouldReturnAllListOfPetsUnfilteredWhenBreedIsBlank() {
            given(petRepository.findAll(any(Specification.class))).willReturn(List.of(pet1, pet2));
            given(petMapper.entityToDto(pet1)).willReturn(petResponse1);
            given(petMapper.entityToDto(pet2)).willReturn(petResponse2);

            List<PetResponse> result = petService.getFilteredPets(null, null, " ");

            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(petResponse1, petResponse2);

            verify(petRepository).findAll(any(Specification.class));
            verify(petMapper).entityToDto(pet1);
            verify(petMapper).entityToDto(pet2);
        }

        @Test
        @DisplayName("Should return all list unfiltered when breed is empty")
        void shouldReturnAllListOfPetsUnfilteredWhenBreedIsEmpty() {
            given(petRepository.findAll(any(Specification.class))).willReturn(List.of(pet1, pet2));
            given(petMapper.entityToDto(pet1)).willReturn(petResponse1);
            given(petMapper.entityToDto(pet2)).willReturn(petResponse2);

            List<PetResponse> result = petService.getFilteredPets(null, null, "");

            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(petResponse1, petResponse2);

            verify(petRepository).findAll(any(Specification.class));
            verify(petMapper).entityToDto(pet1);
            verify(petMapper).entityToDto(pet2);
        }

        @Test
        @DisplayName("Should return all list unfiltered with no params")
        void shouldReturnAllListOfPetsUnfilteredWithNoParams() {
            given(petRepository.findAll(any(Specification.class))).willReturn(List.of(pet1, pet2));
            given(petMapper.entityToDto(pet1)).willReturn(petResponse1);
            given(petMapper.entityToDto(pet2)).willReturn(petResponse2);

            List<PetResponse> result = petService.getFilteredPets(null, null, null);

            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(petResponse1, petResponse2);

            verify(petRepository).findAll(any(Specification.class));
            verify(petMapper).entityToDto(pet1);
            verify(petMapper).entityToDto(pet2);
        }
    }

    @Nested
    @DisplayName("createPet")
    class createPetTests {
        @Test
        @DisplayName("Should create a pet when veterinary is authenticated successfully")
        void shouldCreateAPetWhenVeterinaryAuthenticated() {
            given(userRepository.findByUsernameIgnoreCase("Debora")).willReturn(Optional.of(user));
            given(petMapper.dtoToEntity(petRequestNew, user)).willReturn(petNew);
            given(petRepository.save(petNew)).willReturn(petNew);
            given(petMapper.entityToDto(petNew)).willReturn(petResponseNew);

            PetResponse result = petService.createPet(petRequestNew, userVeterinary);

            assertThat(result).isNotNull();
            assertThat(result.name()).isEqualTo("Trufa");
            assertThat(result.species()).isEqualTo("Perro");
            assertThat(result.username()).isEqualTo("Debora");

            verify(userRepository).findByUsernameIgnoreCase("Debora");
            verify(petMapper).dtoToEntity(petRequestNew, user);
            verify(petRepository).save(petNew);
            verify(petMapper).entityToDto(petNew);
        }

        @Test
        @DisplayName("Should throw an exception when a pet owner username is not found")
        void shouldThrowExceptionWhenUsernameIsNotFound() {
            PetRequest petRequestException = new PetRequest(
                    "Trufa",
                    "Perro",
                    "Caniche Toy",
                    LocalDate.parse("2021-03-15"),
                    "https://example.com/images/trufa.jpg",
                    "Nombre de usuario"
            );

            given(userRepository.findByUsernameIgnoreCase("Nombre de usuario")).willReturn(Optional.empty());

            Throwable throwable = catchThrowable(()->petService.createPet(petRequestException, userVeterinary));

            assertThat(throwable)
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("User not found with username: Nombre de usuario");

            verify(userRepository).findByUsernameIgnoreCase("Nombre de usuario");
        }
    }

    @Nested
    @DisplayName("updatePet")
    class updatePetTests {
        @Test
        @DisplayName("Should update a pet when veterinary is authenticated and data is valid")
        void shouldUpdatePetSuccessfully() {
            given(petRepository.findById(1L)).willReturn(Optional.of(pet1));
            given(userRepository.findByUsernameIgnoreCase("Debora")).willReturn(Optional.of(user));
            given(petMapper.entityToDto(pet1)).willReturn(petResponse1);

            PetResponse petResponse = petService.updatePet(1L, updatedPetRequest, userVeterinary);

            assertThat(petResponse.name()).isEqualTo("Luna");
            assertThat(petResponse.species()).isEqualTo("Perro");
            assertThat(petResponse.username()).isEqualTo("Debora");

            verify(petRepository).findById(1L);
            verify(userRepository).findByUsernameIgnoreCase("Debora");
            verify(petMapper).entityToDto(pet1);
        }

        @Test
        @DisplayName("Should throw exception when petId does not exist")
        void shouldThrowExceptionWhenPetIdDoesNotExist() {
            given(petRepository.findById(999L)).willReturn(Optional.empty());

            Throwable throwable = catchThrowable(()->
                    petService.updatePet(999L, updatedPetRequest, userVeterinary));

            assertThat(throwable)
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("Pet not found with id 999");

            verify(petRepository).findById(999L);
        }

        @Test
        @DisplayName("Should throw exception when username does not exist")
        void shouldThrowExceptionWhenUsernameDoesNotExist() {
            given(petRepository.findById(1L)).willReturn(Optional.of(pet1));
            given(userRepository.findByUsernameIgnoreCase("Nombre de usuario")).willReturn(Optional.empty());

            Throwable throwable = catchThrowable(()->
                    petService.updatePet(1L, updatedPetRequestUsernameInvalid, userVeterinary));

            assertThat(throwable)
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("User not found with username: Nombre de usuario");

            verify(petRepository).findById(1L);
            verify(userRepository).findByUsernameIgnoreCase("Nombre de usuario");
        }
    }

    @Nested
    @DisplayName("deletePet")
    class deletePetTests {
        @Test
        @DisplayName("Should delete pet when user is veterinary and pet exists")
        void shouldDeletePetWhenVeterinaryAndPetExists() {
            given(petRepository.findById(1L)).willReturn(Optional.of(pet1));

            Map<String, String> result = petService.deletePet(1L, userVeterinary);

            assertThat(result.get("message")).isEqualTo("Pet 'Luna' with id:1 has been deleted successfully");
        }

        @Test
        @DisplayName("Should throw exception when petId does not exist")
        void shouldThrowExceptionWhenPetIdDoesNotExist() {
            given(petRepository.findById(999L)).willReturn(Optional.empty());

            Throwable throwable = catchThrowable(()->
                    petService.deletePet(999L, userVeterinary));

            assertThat(throwable)
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("Pet not found with id 999");
        }

        @Test
        @DisplayName("Should throw exception when user is not veterinary")
        void shouldThrowExceptionWhenUserIsNotVeterinary() {
            Throwable throwable = catchThrowable(()->
                    petService.deletePet(1L, userRegular));

            assertThat(throwable)
                    .isInstanceOf(SecurityException.class)
                    .hasMessage("Only veterinaries can manage pets");
        }
    }
}
