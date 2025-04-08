package com.example.bank_cards.integration;

import com.example.bank_cards.entity.CardEntity;
import com.example.bank_cards.entity.UserEntity;
import com.example.bank_cards.enums.Roles;
import com.example.bank_cards.enums.StatusCard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.YearMonth;
import java.util.Base64;

public class DataSetForIntegrationTest {

    public static UserEntity createUser(String email, String password, Roles role) {
        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);
        user.setName("Test User");
        return user;
    }

    public static CardEntity createCard(UserEntity user, String cardNumber, StatusCard status) {
        CardEntity card = new CardEntity();
        card.setOwner(user);
        card.setNumber(Base64.getEncoder().encodeToString(cardNumber.getBytes(StandardCharsets.UTF_8)));
        card.setStatus(status);
        card.setBalance(0.0);
        card.setExpiryDate("04/2028");
        return card;
    }

}
