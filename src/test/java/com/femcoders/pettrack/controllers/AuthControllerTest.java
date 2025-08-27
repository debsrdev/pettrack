package com.femcoders.pettrack.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.femcoders.pettrack.dtos.user.LoginRequest;
import com.femcoders.pettrack.dtos.user.UserRequest;
import lombok.extern.slf4j.Slf4j;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String asJsonString(Object object){
        try{
            return objectMapper.writeValueAsString(object);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    private ResultActions performPostRequest(String url, Object body) throws Exception {
        var requestBuilder = post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(body))
                .accept(MediaType.APPLICATION_JSON);

        return mockMvc.perform(requestBuilder);
    }

    @Nested
    @DisplayName("Post /api/auth/register")
    class RegisterUser {
        @Test
        @DisplayName("Should register a new user successfully")
        void shouldRegisterUserSuccessfully() throws Exception
        {
            UserRequest userRequestDTO = new UserRequest(
                    "mariarubio",
                    "mariarubio@email.com",
                    "Maria1234."
            );
            performPostRequest("/api/auth/register", userRequestDTO)
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.username").value("mariarubio"))
                    .andExpect(jsonPath("$.email").value("mariarubio@email.com"));
        }

        @Test
        @DisplayName("Should return 400 if username is already registered")
        void shouldReturn400IfUsernameIsRegistered() throws Exception
        {
            UserRequest userRequestDTO = new UserRequest(
                    "Debora",
                    "nuevouser@user.com",
                    "Debora123."
            );
            performPostRequest("/api/auth/register", userRequestDTO)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Username already exists"));
        }

        @Test
        @DisplayName("Should return 400 if email is already registered")
        void shouldReturn400IfEmailIsRegistered() throws Exception
        {
            UserRequest userRequestDTO = new UserRequest(
                    "Nuevo user",
                    "debora@user.com",
                    "Debora123."
            );
            performPostRequest("/api/auth/register", userRequestDTO)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Email already registered"));
        }

        @Test
        @DisplayName("Should return 400 when values are invalid")
        void shouldReturn400IfValuesAreInvalid() throws Exception
        {
            UserRequest userRequestDTO = new UserRequest(
                    " ",
                    "email",
                    "123"
            );
            performPostRequest("/api/auth/register", userRequestDTO)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation failed"))
                    .andExpect(jsonPath("$.details.username").value("Username is required"))
                    .andExpect(jsonPath("$.details.email").value("Email not valid"))
                    .andExpect(jsonPath("$.details.password").value("Password must contain a minimum of 8 characters, including a number, one uppercase letter, one lowercase letter and one special character"));
        }
    }

    @Nested
    @DisplayName("Post /api/auth/login")
    class LoginUser {
        @Test
        @DisplayName("Should login a user already registered successfully")
        void shouldLoginSuccessfully() throws Exception
        {
            UserRequest userRequestDTO = new UserRequest(
                    "mariarubio",
                    "mariarubio@email.com",
                    "Maria1234."
            );
            performPostRequest("/api/auth/register", userRequestDTO)
                    .andExpect(status().isCreated());

            LoginRequest loginRequest = new LoginRequest(
                    "mariarubio@email.com",
                    "Maria1234."
            );
            performPostRequest("/api/auth/login", loginRequest)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").exists());
        }

        @Test
        @DisplayName("Should return 401 with wrong password")
        void shouldReturn401WithWrongPassword() throws Exception
        {
            LoginRequest loginRequest = new LoginRequest(
                    "debora@user.com",
                    "Wrongpassword"
            );
            performPostRequest("/api/auth/login", loginRequest)
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Should return 400 with invalid values")
        void shouldReturn400WithInvalidValues() throws Exception
        {
            LoginRequest loginRequest = new LoginRequest(
                    " ",
                    "123"
            );
            performPostRequest("/api/auth/login", loginRequest)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation failed"))
                    .andExpect(jsonPath("$.details.identifier").value("Username or email is required"));
        }
    }
}
