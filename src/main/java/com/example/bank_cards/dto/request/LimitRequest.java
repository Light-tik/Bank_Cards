package com.example.bank_cards.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Запрос на установку месячного лимита по карте")
public class LimitRequest {

    @Schema(description = "ID карты", requiredMode = Schema.RequiredMode.REQUIRED, example = "444e4567-e89b-12d3-a456-426614174003")
    @NotNull(message = "ID карты обязателен")
    private UUID cardId;

    @Schema(description = "Месячный лимит", requiredMode = Schema.RequiredMode.REQUIRED, example = "5000.00")
    @NotNull(message = "Лимит обязателен")
    @DecimalMin(value = "0.01", message = "Лимит должен быть больше 0")
    private Double monthlyLimit;
}

