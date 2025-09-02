package com.femcoders.pettrack.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.femcoders.pettrack.models.Role;
import com.femcoders.pettrack.models.User;
import com.femcoders.pettrack.security.UserDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("PetController Integration Tests")
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class PetControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String asJsonString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    private ResultActions performGetRequest(String url, Map<String, String> params) throws Exception {
        var requestBuilder = get(url)
                .with(user("testuser").roles("USER"))
                .accept(MediaType.APPLICATION_JSON);

        if (params != null) {
            params.forEach((key, value) -> requestBuilder.param(key, value));
        }

        return mockMvc.perform(requestBuilder);
    }

    private ResultActions performPostRequest(String url, Object body, UserDetail userDetail) throws Exception {
        return mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(body))
                .with(user(userDetail))
                .accept(MediaType.APPLICATION_JSON));
    }

    private ResultActions performPutRequest(String url, Object body, UserDetail userDetail) throws Exception {
        return mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(body))
                .with(user(userDetail))
                .accept(MediaType.APPLICATION_JSON));
    }

    private ResultActions performDeleteRequest(String url,UserDetail userDetail) throws Exception {
        return mockMvc.perform(delete(url)
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(userDetail))
                .accept(MediaType.APPLICATION_JSON));
    }

    @Nested
    @DisplayName("Get /api/pets")
    class getAllPetsTest {
        @Test
        @DisplayName("Should return a list of all pets with status 200 OK and correct content type")
        void getAllPets_returnsListOfPets() throws Exception {
            performGetRequest("/api/pets", null)
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(12)))
                    .andExpect(jsonPath("$[0].name", is("Luna")))
                    .andExpect(jsonPath("$[0].species", is("Perro")))
                    .andExpect(jsonPath("$[9].breed", is("Chihuahua")));
        }

        @Test
        @DisplayName("Should return pets with expected structure and data types")
        void getAllPets_returnsCorrectStructureAndTypes() throws Exception {
            performGetRequest("/api/pets", null)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").isNumber())
                    .andExpect(jsonPath("$[0].name").isString())
                    .andExpect(jsonPath("$[0].species").isString())
                    .andExpect(jsonPath("$[0].breed").isString())
                    .andExpect(jsonPath("$[0].birthDate").isString())
                    .andExpect(jsonPath("$[0].birthDate", matchesPattern("\\d{4}-\\d{2}-\\d{2}")))
                    .andExpect(jsonPath("$[0].username").isString());
        }
    }

    @Nested
    @DisplayName("Get /api/pets/filter")
    class getFilteredPetsTests {
        @Test
        @DisplayName("Should return pets filtered by name, species and/or breed")
        void getFilteredPets_returnFilteredResults() throws Exception {
            performGetRequest("/api/pets/filter", Map.of(
                    "name", "Luna",
                    "species", "perro",
                    "breed", "golden"
            ))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", not(empty())))
                    .andExpect(jsonPath("$[?(@.name == 'Luna')]").exists());
        }

        @Test
        @DisplayName("Should return all pets when no filter is applied")
        void getFilteredPets_returnAllWhenNoParams() throws Exception {
            performGetRequest("/api/pets/filter", null)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(12)));
        }

        @Test
        @DisplayName("Should return empty list when filter has no matches")
        void getFilteredPets_returnEmptyWhenNoMatch() throws Exception {
            performGetRequest("/api/pets/filter", Map.of(
                    "name", "nombrequenoexiste"
            ))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", empty()));
        }
    }

    @Nested
    @DisplayName("Get /api/pets/{id}")
    class GetPetByIdTests {
        private final Long PET_ID_VALID = 1L;
        private final Long PET_ID_NOT_VALID = 100L;

        @Test
        @DisplayName("Should return pet by ID with existing ID")
        void getPetById_returnsPet_whenIdExists() throws Exception {
            performGetRequest("/api/pets/" + PET_ID_VALID, null)
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(PET_ID_VALID.intValue())))
                    .andExpect(jsonPath("$.name", is("Luna")));

        }

        @Test
        @DisplayName("Should not return pet by ID with non existing ID")
        void getPetById_returnsNotFound_whenIdNotExists() throws Exception {
            performGetRequest("/api/pets/" + PET_ID_NOT_VALID, null)
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Pet not found with id 100"))
                    .andExpect(jsonPath("$.timestamp").exists());

        }
    }

    @Nested
    @DisplayName("Post /api/pets")
    class CreatePetTests {
        private User veterinaryUser;
        private UserDetail veterinaryUserDetail;
        private User regularUser;
        private UserDetail regularUserDetail;
        private Map<String, String> petRequest;
        private Map<String, String> petRequestInvalid;

        @BeforeEach
        void setup() {
            petRequest = Map.of(
                    "name", "Trufa",
                    "species", "Perro",
                    "breed", "Caniche Toy",
                    "birthDate", "2021-03-15",
                    "image", "https://example.com/images/trufa.jpg",
                    "username", "Debora"
            );

            petRequestInvalid = Map.of(
                    "name", "Trufa",
                    "species", "Perro",
                    "breed", "Caniche Toy",
                    "birthDate", "2021-03-15",
                    "image", "https://example.com/images/trufa.jpg",
                    "username", "No Existe"
            );

            veterinaryUser = User.builder()
                    .id(99L)
                    .username("VeterinaryTest")
                    .role(Role.VETERINARY)
                    .build();
            veterinaryUserDetail = new UserDetail(veterinaryUser);

            regularUser = User.builder()
                    .id(55L)
                    .username("NoVeterinaryTest")
                    .role(Role.USER)
                    .build();
            regularUserDetail = new UserDetail(regularUser);
        }

        @Test
        @DisplayName("Should create pet when user is veterinary and data is valid")
        void createPet_returnsPet_whenVeterinaryAndValid() throws Exception {
            performPostRequest("/api/pets", petRequest, veterinaryUserDetail)
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.name").value("Trufa"))
                    .andExpect(jsonPath("$.species").value("Perro"))
                    .andExpect(jsonPath("$.breed").value("Caniche Toy"))
                    .andExpect(jsonPath("$.username").value("Debora"));
        }

        @Test
        @DisplayName("Should return 404 when pet owner user does not exist")
        void createPet_returnsNotFound_whenUsernameDoesNotExist() throws Exception {
            performPostRequest("/api/pets", petRequestInvalid, veterinaryUserDetail)
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("User not found with username: No Existe"))
                    .andExpect(jsonPath("$.timestamp").exists());
        }

        @Test
        @DisplayName("Should return 401 when user is not a veterinary")
        void createPet_returnsForbidden_whenUserIsNotVeterinary() throws Exception {
            performPostRequest("/api/pets", petRequest, regularUserDetail)
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value("Only veterinaries can manage pets"))
                    .andExpect(jsonPath("$.timestamp").exists());
        }
    }

    @Nested
    @DisplayName("Put /api/pets")
    class UpdatePetTests {
        private UserDetail vetUserDetail;
        private UserDetail regularUserDetail;
        private Map<String, String> validRequest;
        private Map<String, String> requestWithNonExistingUser;

        @BeforeEach
        void setup() {
            vetUserDetail = new UserDetail(User.builder()
                    .id(100L)
                    .username("VeterinaryTest")
                    .role(Role.VETERINARY)
                    .build());

            regularUserDetail = new UserDetail(User.builder()
                    .id(101L)
                    .username("RegularUser")
                    .role(Role.USER)
                    .build());

            validRequest = Map.of(
                    "name", "UpdatedName",
                    "species", "Perro",
                    "breed", "Labrador",
                    "birthDate", "2020-01-01",
                    "image", "https://example.com/updated.jpg",
                    "username", "Debora"
            );

            requestWithNonExistingUser = Map.of(
                    "name", "AnotherName",
                    "species", "Perro",
                    "breed", "Labrador",
                    "birthDate", "2020-01-01",
                    "image", "https://example.com/updated.jpg",
                    "username", "NoExiste"
            );
        }

        @Test
        @DisplayName("Should update pet when ID and username exist and role is veterinary")
        void updatePet_successful() throws Exception {
            performPutRequest("/api/pets/1", validRequest, vetUserDetail)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("UpdatedName"))
                    .andExpect(jsonPath("$.species").value("Perro"))
                    .andExpect(jsonPath("$.username").value("Debora"));
        }

        @Test
        @DisplayName("Should return 404 when petId does not exist")
        void updatePet_petIdNotFound() throws Exception {
            performPutRequest("/api/pets/999", validRequest, vetUserDetail)
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Pet not found with id 999"))
                    .andExpect(jsonPath("$.timestamp").exists());
        }

        @Test
        @DisplayName("Should return 404 when username does not exist")
        void updatePet_usernameNotFound() throws Exception {
            performPutRequest("/api/pets/1", requestWithNonExistingUser, vetUserDetail)
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("User not found with username: NoExiste"))
                    .andExpect(jsonPath("$.timestamp").exists());
        }

        @Test
        @DisplayName("Should return 403 when user is not veterinary")
        void updatePet_forbiddenWhenIsNotVeterinary() throws Exception {
            performPutRequest("/api/pets/1", validRequest, regularUserDetail)
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value("Only veterinaries can manage pets"))
                    .andExpect(jsonPath("$.timestamp").exists());
        }
    }

    @Nested
    @DisplayName("Delete /api/pets")
    class DeletePetTests {
        private UserDetail vetUserDetail;
        private UserDetail regularUserDetail;
        private Map<String, String> validRequest;
        private Map<String, String> requestWithNonExistingUser;

        @BeforeEach
        void setup() {
            vetUserDetail = new UserDetail(User.builder()
                    .id(100L)
                    .username("VeterinaryTest")
                    .role(Role.VETERINARY)
                    .build());

            regularUserDetail = new UserDetail(User.builder()
                    .id(101L)
                    .username("RegularUser")
                    .role(Role.USER)
                    .build());

            validRequest = Map.of(
                    "name", "UpdatedName",
                    "species", "Perro",
                    "breed", "Labrador",
                    "birthDate", "2020-01-01",
                    "image", "https://example.com/updated.jpg",
                    "username", "Debora"
            );

            requestWithNonExistingUser = Map.of(
                    "name", "AnotherName",
                    "species", "Perro",
                    "breed", "Labrador",
                    "birthDate", "2020-01-01",
                    "image", "https://example.com/updated.jpg",
                    "username", "NoExiste"
            );
        }

        @Test
        @DisplayName("Should delete pet when ID and username exist and role is veterinary")
        void deletePet_successful() throws Exception {
            performDeleteRequest("/api/pets/1", vetUserDetail)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Pet 'Luna' with id:1 has been deleted successfully"));
        }

        @Test
        @DisplayName("Should return 404 when pet does not exist")
        void deletePet_returnsNotFoundWhenPetNotExist() throws Exception {
            performDeleteRequest("/api/pets/1000", vetUserDetail)
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Pet not found with id 1000"))
                    .andExpect(jsonPath("$.timestamp").exists());
        }

        @Test
        @DisplayName("Should return 404 when is not veterinary")
        void deletePet_returnsForbiddenWhenIsNotVeterinary() throws Exception {
            performDeleteRequest("/api/pets/1", regularUserDetail)
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value("Only veterinaries can manage pets"))
                    .andExpect(jsonPath("$.timestamp").exists());
        }
    }
}