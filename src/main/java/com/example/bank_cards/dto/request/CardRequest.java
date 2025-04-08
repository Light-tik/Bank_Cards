package com.example.bank_cards.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Запрос на создание карты")
public class CardRequest {

    @Schema(description = "ID владельца карты", requiredMode = Schema.RequiredMode.REQUIRED, example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    @NotNull(message = "ID card's owner not be null")
    private UUID ownerId;

    @Schema(description = "Номер карты (16 цифр)", requiredMode = Schema.RequiredMode.REQUIRED, example = "1234567812345678")
    @NotBlank(message = "Номер карты не может быть пустым")
    @Pattern(regexp = "\\d{16}", message = "Номер карты должен состоять из 16 цифр")
    private String cardNumber;

    @Schema(description = "Срок действия карты (в формате MM/YYYY)", requiredMode = Schema.RequiredMode.REQUIRED, example = "2030-12")
    @NotNull(message = "Срок действия карты обязателен")
    @Pattern(regexp = "^([1-9]|1[012])/(\\d{4})$",
            message = "Неверный формат даты. Используйте формат ММ/ГГГГ")
    private String expiryDate;
}

