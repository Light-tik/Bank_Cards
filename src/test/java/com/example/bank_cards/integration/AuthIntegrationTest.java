package com.example.bank_cards.integration;

import com.example.bank_cards.dto.request.SignInRequest;
import com.example.bank_cards.dto.request.SignUpRequest;
import com.example.bank_cards.entity.UserEntity;
import com.example.bank_cards.enums.Roles;
import com.example.bank_cards.repositories.UserRepository;
import com.example.bank_cards.validation.ErrorCodes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AuthIntegrationTest {

    private MockMvc mockMvc;

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setMockMvc(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @BeforeEach
    @Transactional
    public void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    public void shouldRegisterUserSuccessfully() throws Exception {
        SignUpRequest signUpRequest = new SignUpRequest("user@example.com", "Иван", "password123", Roles.ROLE_USER);
        mockMvc.perform(post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(signUpRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("user@example.com"))
                .andExpect(jsonPath("$.data.token").isNotEmpty());
        assertTrue(userRepository.existsByEmail("user@example.com"));
    }

    @Test
    @Transactional
    public void shouldReturnBadRequestWhenEmailAlreadyExists() throws Exception {
        SignUpRequest signUpRequest = new SignUpRequest("user@example.com", "Иван", "password123", Roles.ROLE_USER);
        String encodedPassword = passwordEncoder.encode("password123");
        userRepository.save(new UserEntity(null, "user@example.com", "Иван", Roles.ROLE_USER, encodedPassword));

        mockMvc.perform(post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(signUpRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(ErrorCodes.USER_ALREADY_EXISTS.getCode()));
    }

    @Test
    @Transactional
    public void shouldLoginUserSuccessfully() throws Exception {
        String encodedPassword = passwordEncoder.encode("password123");
        userRepository.save(new UserEntity(null, "user@example.com", "Иван", Roles.ROLE_USER, encodedPassword));

        SignInRequest signInRequest = new SignInRequest("user@example.com", "password123");

        mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(signInRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("user@example.com"))
                .andExpect(jsonPath("$.data.token").isNotEmpty());
    }


    @Test
    @Transactional
    public void shouldReturnUnauthorizedWhenInvalidCredentials() throws Exception {
        userRepository.save(new UserEntity(
                null, "user@example.com", "Имя", Roles.ROLE_USER, passwordEncoder.encode("correctpassword")
        ));

        SignInRequest signInRequest = new SignInRequest("user@example.com", "wrongpassword");

        mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(signInRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.statusCode").value(ErrorCodes.PASSWORD_NOT_VALID.getCode()));
    }

    @Test
    @Transactional
    public void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        SignInRequest signInRequest = new SignInRequest("non1existent@example.com", "password123");

        mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(signInRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(ErrorCodes.USER_NOT_FOUND.getCode()));
    }

    public static String asJsonString(Object obj) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert object to JSON string", e);
        }
    }
}
