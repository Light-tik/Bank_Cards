package com.example.bank_cards;

import com.example.bank_cards.dto.request.*;
import com.example.bank_cards.dto.response.*;
import com.example.bank_cards.entity.CardEntity;
import com.example.bank_cards.entity.LimitEntity;
import com.example.bank_cards.entity.TransactionEntity;
import com.example.bank_cards.entity.UserEntity;
import com.example.bank_cards.enums.StatusCard;
import com.example.bank_cards.enums.Roles;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DataSet {

    private static final UUID cardId = UUID.randomUUID();

    public static UserEntity getUser() {
        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setRole(Roles.ROLE_USER);
        user.setPassword("encodedPassword");
        return user;
    }

    public static CardEntity getCard(UserEntity owner) {
        return new CardEntity(
                cardId,
                "1234567812345678",
                owner,
                "2030/11",
                StatusCard.ACTIVE,
                1000.0
        );
    }

    public static CardRequest getCardRequest(CardEntity card) {
        return new CardRequest(
                card.getOwner().getId(),
                card.getNumber(),
                card.getExpiryDate()
        );
    }

    public static CardResponse getCardResponse(CardEntity card) {
        return new CardResponse(
                card.getId(),
                maskCardNumber(card.getNumber()),
                card.getOwner().getId(),
                card.getExpiryDate(),
                card.getStatus(),
                card.getBalance()
        );
    }

    private static String maskCardNumber(String number) {
        if (number == null || number.length() < 4) return "****";
        return "**** **** **** " + number.substring(number.length() - 4);
    }

    public static TransactionEntity getTransaction(CardEntity card, double amount) {
        return new TransactionEntity(
                UUID.randomUUID(),
                card,
                amount,
                LocalDateTime.now(),
                null
        );
    }

    public static List<TransactionEntity> getTransactionList(CardEntity card, int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> getTransaction(card, 100.0 + i))
                .collect(Collectors.toList());
    }

    public static List<TransactionResponse> getTransactionResponseList(CardEntity card, int count) {
        return getTransactionList(card, count).stream()
                .map(DataSet::getTransactionResponse)
                .collect(Collectors.toList());
    }

    public static TransactionResponse getTransactionResponse(TransactionEntity tx) {
        return new TransactionResponse(
                tx.getId(),
                tx.getAmount(),
                tx.getTimestamp(),
                tx.getCard().getId()
        );
    }

    public static Page<TransactionEntity> getTransactionEntityPage(CardEntity card, int count, int page, int size) {
        List<TransactionEntity> list = getTransactionList(card, count);
        return new PageImpl<>(list, PageRequest.of(page, size), count);
    }

    // ========== Лимит ==========
    public static LimitEntity getLimit(CardEntity card) {
        return new LimitEntity(
                UUID.randomUUID(),
                5000.0,
                1000.0,
                LocalDateTime.now().minusDays(5),
                card
        );
    }

    public static LimitRequest getLimitRequest(CardEntity card) {
        return new LimitRequest(
                card.getId(),
                5000.0
        );
    }

    public static LimitResponse getLimitResponse(LimitEntity entity) {
        return new LimitResponse(
                entity.getCard().getId(),
                entity.getMonthlyLimit(),
                entity.getUsedThisMonth()
        );
    }

    public static TransactionRequest getTransferRequest(UUID fromCardId, UUID toCardId, Double amount) {
        return new TransactionRequest(fromCardId, toCardId, amount);
    }

    public static Page<CardEntity> createPageEntity() {
        return new PageImpl<>(List.of(getCard(getUser())));
    }

    // ========== Авторизация и регистрация ==========
    public static SignUpRequest createUserRequest(UserEntity user) {
        return new SignUpRequest(
                user.getEmail(),
                user.getName(),
                user.getPassword(),
                user.getRole()
        );
    }

    public static SignInRequest createAuthUserRequest(UserEntity user) {
        return new SignInRequest(
                user.getEmail(),
                user.getPassword()
        );
    }

    public static LoginUserResponse createLoginUserResponse(UserEntity user) {
        return new LoginUserResponse(
                user.getEmail(),
                user.getId(),
                user.getName(),
                user.getRole(),
                MyTestConstants.TOKEN
        );
    }

    public static PublicUserView createPublicUserView(UserEntity user) {
        return new PublicUserView(
                user.getEmail(),
                user.getId(),
                user.getName(),
                user.getRole()
        );
    }
}
