package com.femcoders.pettrack.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.femcoders.pettrack.models.Role;
import com.femcoders.pettrack.models.User;
import com.femcoders.pettrack.security.UserDetail;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayName("MedicalRecordController Integration Tests")
public class MedicalRecordControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private UserDetail vet;
    private UserDetail regular;

    @BeforeEach
    void setup() {
        // Usa un vet REAL del test-data.sql
        vet = new UserDetail(
                User.builder()
                        .id(4L)
                        .username("Carmen")
                        .role(Role.VETERINARY)
                        .build()
        );

        regular = new UserDetail(
                User.builder()
                        .id(1L)
                        .username("Debora")
                        .role(Role.USER)
                        .build()
        );
    }

    private String asJson(Object o) {
        try { return objectMapper.writeValueAsString(o); }
        catch (Exception e) { throw new RuntimeException(e); }
    }

    @Nested
    @DisplayName("POST /api/medical-records")
    class Create {
        @Test
        @DisplayName("Should create record (201) when vet")
        void shouldCreate_whenVet() throws Exception {
            var body = Map.of(
                    "description", "Vacuna",
                    "weight", 12.0,
                    "date", LocalDate.now().toString(),
                    "type", "VACCINATION",
                    "petId", 1
            );

            mockMvc.perform(post("/api/medical-records")
                            .with(user(vet))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJson(body))
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.description").value("Vacuna"));
        }

        @Test
        @DisplayName("Should return 403 when not vet")
        void shouldReturnForbidden_whenNotVet() throws Exception {
            var body = Map.of(
                    "description", "Vacuna",
                    "weight", 12.0,
                    "date", LocalDate.now().toString(),
                    "type", "VACCINATION",
                    "petId", "1"
            );

            mockMvc.perform(post("/api/medical-records")
                            .with(user(regular))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJson(body)))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").exists());
        }

        @Test
        @DisplayName("Should return 400 when validation fails")
        void shouldReturnBadRequest_whenInvalid() throws Exception {
            var body = Map.of(
                    "date", LocalDate.now().toString(),
                    "type", "VACCINATION",
                    "petId", "1"
            );

            mockMvc.perform(post("/api/medical-records")
                            .with(user(vet))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJson(body)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation failed"));
        }

        @Test
        @DisplayName("Should return 404 when pet does not exist")
        void shouldReturnNotFound_whenPetMissing() throws Exception {
            var body = Map.of(
                    "description", "Vacuna",
                    "weight", 12.0,
                    "date", LocalDate.now().toString(),
                    "type", "VACCINATION",
                    "petId", "99999"
            );

            mockMvc.perform(post("/api/medical-records")
                            .with(user(vet))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJson(body)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Pet not found with id 99999"));
        }
    }

    @Nested
    @DisplayName("GET /api/medical-records")
    class GetAll {
        @Test
        @DisplayName("Should return list of medical records")
        void shouldReturnList() throws Exception {
            mockMvc.perform(get("/api/medical-records")
                            .with(user(vet))
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());
        }
    }

    @Nested
    @DisplayName("GET /api/medical-records/{id}")
    class GetById {
        @Test
        @DisplayName("Should return a record by ID")
        void shouldReturnById() throws Exception {
            mockMvc.perform(get("/api/medical-records/1")
                            .with(user(vet))
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1));
        }

        @Test
        @DisplayName("Should return 404 when not found")
        void shouldReturnNotFound() throws Exception {
            mockMvc.perform(get("/api/medical-records/999")
                            .with(user(vet)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("MedicalRecord not found with id 999"));
        }
    }
    @Nested
    @DisplayName("DELETE /api/medical-records/{id}")
    class Delete {
        @Test
        @DisplayName("Should delete record")
        void shouldDelete() throws Exception {
            mockMvc.perform(delete("/api/medical-records/1")
                            .with(user(vet)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").exists());
        }

        @Test
        @DisplayName("Should return 404 if not found")
        void shouldReturnNotFound() throws Exception {
            mockMvc.perform(delete("/api/medical-records/999")
                            .with(user(vet)))
                    .andExpect(status().isNotFound());
        }
    }
}
