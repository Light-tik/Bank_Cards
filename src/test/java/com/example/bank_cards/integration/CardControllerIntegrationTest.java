package com.example.bank_cards.integration;

import com.example.bank_cards.dto.request.CardRequest;
import com.example.bank_cards.entity.CardEntity;
import com.example.bank_cards.entity.UserEntity;
import com.example.bank_cards.enums.Roles;
import com.example.bank_cards.enums.StatusCard;
import com.example.bank_cards.repositories.CardRepository;
import com.example.bank_cards.repositories.UserRepository;
import com.example.bank_cards.security.JwtService;
import com.example.bank_cards.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.junit.jupiter.Container;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.time.YearMonth;

import static com.example.bank_cards.integration.DataSetForIntegrationTest.createCard;
import static com.example.bank_cards.integration.DataSetForIntegrationTest.createUser;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class CardControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");

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

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
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
    void testCreateCardAsAdmin() throws Exception {

        CardRequest request = new CardRequest();
        request.setOwnerId(adminUser.getId());
        request.setCardNumber("1234567812345678");
        request.setExpiryDate("04/2028");

        mockMvc.perform(post("/api/admin/cards")
                        .header("Authorization", tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void testCreateCardAsUser() throws Exception {
        CardRequest request = new CardRequest();
        request.setOwnerId(adminUser.getId());
        request.setCardNumber("1234567812345678");
        request.setExpiryDate("04/2028");

        mockMvc.perform(post("/api/admin/cards")
                        .header("Authorization", tokenUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetCardByIdAsAdmin() throws Exception {
        mockMvc.perform(get("/api/admin/cards/" + card.getId())
                .header("Authorization", tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ownerId").value(adminUser.getId()))
                .andExpect(jsonPath("$.maskedNumber").value("****-****-****-5678"));
    }

    @Test
    void testGetCardByIdAsUser() throws Exception {
        mockMvc.perform(get("/api/admin/cards/" + card.getId())
                .header("Authorization", tokenUser))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetCardsWithPaginationAsUser() throws Exception {
        for (int i = 0; i < 5; i++) {
            createCard(adminUser, "11112222333344" + i, StatusCard.ACTIVE);
        }

        mockMvc.perform(get("/api/user/cards/page")
                        .param("page", "0")
                        .param("size", "3")
                .header("Authorization", tokenUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.totalElements").value(5));
    }


    @Test
    void testGetCardsWithPagination() throws Exception {
        for (int i = 0; i < 5; i++) {
            createCard(adminUser, "11112222333344" + i, StatusCard.ACTIVE);
        }

        mockMvc.perform(get("/api/admin/cards/page")
                        .param("page", "0")
                        .param("size", "3")
                        .header("Authorization", tokenUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.totalElements").value(5));
    }

    @Test
    void testSearchCardByStatus() throws Exception {
        CardEntity card = createCard(adminUser, "9999888877776666", StatusCard.BLOCKED);

        mockMvc.perform(get("/api/admin/cards/search")
                        .param("status", "BLOCKED")
                        .header("Authorization", tokenUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].maskedNumber").value("****-****-****-6666"));
    }
}
