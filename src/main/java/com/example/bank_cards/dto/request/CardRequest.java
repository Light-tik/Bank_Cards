package com.example.bank_cards.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.YearMonth;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CardRequest {

    @NotNull(message = "ID card's owner not be null")
    private UUID ownerId;

    @NotBlank(message = "Номер карты не может быть пустым")
    @Pattern(regexp = "\\d{16}", message = "Номер карты должен состоять из 16 цифр")
    private String cardNumber;

    @NotNull(message = "Срок действия карты обязателен")
    @Future(message = "Срок действия должен быть в будущем")
    private YearMonth expiryDate;
}

