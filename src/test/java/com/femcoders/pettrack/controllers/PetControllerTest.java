package com.femcoders.pettrack.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
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

    private ResultActions performGetRequest(String url, Map<String, String> params) throws Exception {
        var requestBuilder = get(url)
                .with(user("testuser").roles("USER"))
                .accept(MediaType.APPLICATION_JSON);

        if (params != null) {
            params.forEach((key, value) -> requestBuilder.param(key, value));
        }

        return mockMvc.perform(requestBuilder);
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
                    .andExpect(jsonPath("$[0].birthDate" , matchesPattern("\\d{4}-\\d{2}-\\d{2}")))
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
}
