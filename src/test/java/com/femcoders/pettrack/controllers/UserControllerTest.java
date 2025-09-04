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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("UserController Integration Tests")
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDetail userDetailVet;
    private UserDetail userDetailUser;

    @BeforeEach
    void setup() {
        userDetailVet = new UserDetail(
                User.builder()
                        .id(900L)
                        .username("VetTest")
                        .role(Role.VETERINARY)
                        .build()
        );
        userDetailUser = new UserDetail(
                User.builder()
                        .id(901L)
                        .username("UserTest")
                        .role(Role.USER)
                        .build()
        );
    }

    private String asJsonString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    private ResultActions performGetRequest(String url, Map<String, String> params, UserDetail userDetail) throws Exception {
        var requestBuilder = get(url)
                .with(user(userDetail))
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
    @DisplayName("Get /api/users")
    class GetAllUsersTests {
        @Test
        @DisplayName("Should return all users")
        void getAllUsers_returnsAListOfAllUsers() throws Exception {
            performGetRequest("/api/users", null, userDetailVet)
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", not(empty())))
                    .andExpect(jsonPath("$[0].id").isNumber())
                    .andExpect(jsonPath("$[0].username").isString())
                    .andExpect(jsonPath("$[0].role").isString());
        }

        @Test
        @DisplayName("Should return forbidden if not a vet")
        void getAllUsers_returnsForbiddenIfNotAVet() throws Exception {
            performGetRequest("/api/users", null, userDetailUser)
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value("Only veterinarians can view all users"))
                    .andExpect(jsonPath("$.timestamp").exists());
        }
    }

    @Nested
    @DisplayName("Get /api/users/filter")
    class FilterUsersTests {
        @Test
        @DisplayName("Should return users with role equals VETERINARY")
        void getAllUsers_returnsUsersFilteredByRole() throws Exception {
            performGetRequest("/api/users/filter", Map.of("role", "VETERINARY"), userDetailVet)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[*].role", everyItem(is("VETERINARY"))));
        }

        @Test
        @DisplayName("Should return forbidden list if is not a vet")
        void getAllUsers_returnsForbiddenListWhenNoAuth() throws Exception {
            performGetRequest("/api/users/filter", null, userDetailUser)
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value("Only veterinarians can filter users"))
                    .andExpect(jsonPath("$.timestamp").exists());
        }
    }

    @Nested
    @DisplayName("Get /api/users/{id}")
    class GetUserByIdTests {
        @Test
        @DisplayName("Should return user by id")
        void getUserById() throws Exception {
            performGetRequest("/api/users/1", null, userDetailVet)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.username").exists());
        }

        @Test
        @DisplayName("Should return nothing if user does not exist")
        void getNotFound_whenUserDoesNotExist() throws Exception {
            performGetRequest("/api/users/999999", null, userDetailUser)
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("User not found with id 999999"))
                    .andExpect(jsonPath("$.timestamp").exists());
        }
    }

    @Nested
    @DisplayName("Post /api/users")
    class CreateUserTests {

        private Map<String, String> userRequest;

        @BeforeEach
        void setup() {
            userRequest = Map.of(
                    "username", "NuevoUser",
                    "email", "nuevouser@email.com",
                    "password", "Nuevouser123."
            );
        }
        @Test
        @DisplayName("Should create user when user is veterinary")
        void createUser_whenVeterinary() throws Exception {
            performPostRequest("/api/users", userRequest, userDetailVet)
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.username").value("NuevoUser"));
        }

        @Test
        @DisplayName("Should not create user when user is not a veterinary")
        void getForbidden_whenUserIsNotVet() throws Exception {
            performPostRequest("/api/users", userRequest, userDetailUser)
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value("Only veterinarians can create users"))
                    .andExpect(jsonPath("$.timestamp").exists());
        }
    }

    @Nested
    @DisplayName("Put /api/users/{id}")
    class UpdateUserTests {

        private Map<String, String> userRequestUpdated;

        @BeforeEach
        void setup() {
            userRequestUpdated = Map.of(
                    "username", "NuevoUserUpdated",
                    "email", "nuevouserupdated@email.com",
                    "password", "Nuevouserupdated123."
            );
        }
        @Test
        @DisplayName("Should update user when user is veterinary")
        void updateUser_whenVeterinary() throws Exception {
            performPutRequest("/api/users/1", userRequestUpdated, userDetailVet)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("NuevoUserUpdated"));
        }

        @Test
        @DisplayName("Should not create user when id is not found")
        void getNotFound_whenUserIdNotExists() throws Exception {
            performPutRequest("/api/users/9999999", userRequestUpdated, userDetailVet)
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("User not found with id 9999999"))
                    .andExpect(jsonPath("$.timestamp").exists());
        }
    }

    @Nested
    @DisplayName("Delete /api/users/{id}")
    class DeleteUserTests {
        @Test
        @DisplayName("Should delete user when user is veterinary")
        void deleteUser_whenVeterinary() throws Exception {
            performDeleteRequest("/api/users/1", userDetailVet)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message", containsString("has been deleted")));
        }

        @Test
        @DisplayName("Should not delete user when user is not veterinary")
        void deleteUser_whenIsNotVeterinary() throws Exception {
            performDeleteRequest("/api/users/1", userDetailUser)
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value("Only veterinarians can delete users"))
                    .andExpect(jsonPath("$.timestamp").exists());
        }
    }
}
