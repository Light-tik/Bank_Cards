package com.example.bank_cards.dto.response;

import com.example.bank_cards.enums.StatusCard;
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
@Schema(description = "Ответ с данными карты")
public class CardResponse {

    @Schema(description = "ID карты", example = "111e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(description = "Маскированный номер карты", example = "**** **** **** 1234")
    private String maskedNumber;

    @Schema(description = "ID владельца", example = "222e4567-e89b-12d3-a456-426614174001")
    private UUID ownerId;

    @Schema(description = "Срок действия карты", example = "2027-08")
    private String expiryDate;

    @Schema(description = "Статус карты", example = "ACTIVE")
    private StatusCard status;

    @Schema(description = "Баланс карты", example = "1000.50")
    private Double balance;
}

