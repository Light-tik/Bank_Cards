package com.example.bank_cards.integration;

import com.example.bank_cards.dto.request.CardRequest;
import com.example.bank_cards.dto.request.TransactionRequest;
import com.example.bank_cards.entity.CardEntity;
import com.example.bank_cards.entity.UserEntity;
import com.example.bank_cards.enums.Roles;
import com.example.bank_cards.enums.StatusCard;
import com.example.bank_cards.repositories.CardRepository;
import com.example.bank_cards.repositories.UserRepository;
import com.example.bank_cards.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static com.example.bank_cards.integration.DataSetForIntegrationTest.createCard;
import static com.example.bank_cards.integration.DataSetForIntegrationTest.createUser;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class TransactionServiceIntegrationTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private UserRepository userRepository;

    private JwtService jwtService;

    private CardRepository cardRepository;

    private UserEntity adminUser;

    private UserEntity regularUser;

    private String tokenAdmin;

    private String tokenUser;

    private CardEntity card;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public  void setJwtService(JwtService jwtService) {
        this.jwtService = jwtService;
    }
    @Autowired
    public void setMockMvc(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setCardRepository(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    @BeforeEach
    void setUp() {
        cardRepository.deleteAll();
        userRepository.deleteAll();

        adminUser = createUser("admin@test.com", passwordEncoder.encode("password"), Roles.ROLE_ADMIN);
        regularUser = createUser("user@test.com", "password", Roles.ROLE_USER);
        userRepository.save(adminUser);
        userRepository.save(regularUser);
        card = createCard(adminUser, "1234567812345678", StatusCard.ACTIVE);
        cardRepository.save(card);

        tokenAdmin = jwtService.generateToken(adminUser);
        tokenUser = jwtService.generateToken(regularUser);

    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testTransactionLimit() throws Exception {
        String token = jwtService.generateToken(regularUser);

        CardRequest cardRequest = new CardRequest(regularUser.getId(), "1234567812345678", "08/2029");
        mockMvc.perform(post("/api/cards")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardRequest)))
                .andExpect(status().isCreated());
        TransactionRequest transactionRequest = new TransactionRequest(card.getId(),card.getId(), 700000000.00);
        mockMvc.perform(post("/api/transactions")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Limit exceeded"));
    }

}
