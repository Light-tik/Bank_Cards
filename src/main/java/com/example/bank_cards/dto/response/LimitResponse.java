package com.example.bank_cards.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Ответ с информацией о лимите")
public class LimitResponse {

    @Schema(description = "ID карты", example = "555e4567-e89b-12d3-a456-426614174004")
    private UUID cardId;

    @Schema(description = "Месячный лимит", example = "5000.00")
    private Double monthlyLimit;

    @Schema(description = "Сумма, использованная за текущий месяц", example = "1500.00")
    private Double usedThisMonth;
}

