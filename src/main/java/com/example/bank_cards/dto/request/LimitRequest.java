package com.example.bank_cards.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LimitRequest {

    @NotNull(message = "ID карты обязателен")
    private UUID cardId;

    @NotNull(message = "Лимит обязателен")
    @DecimalMin(value = "0.01", message = "Лимит должен быть больше 0")
    private Double monthlyLimit;
}

