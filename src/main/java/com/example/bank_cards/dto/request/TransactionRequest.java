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
@Schema(description = "Запрос на перевод между картами")
public class TransactionRequest {

    @Schema(description = "ID карты отправителя", example = "a1234567-89ab-cdef-0123-456789abcdef",requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "ID карты отправителя обязателен")
    private UUID fromCardId;

    @Schema(description = "ID карты получателя", example = "b1234567-89ab-cdef-0123-456789abcdef", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "ID карты получателя обязателен")
    private UUID toCardId;

    @Schema(description = "Сумма перевода", example = "100.00", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Сумма обязательна")
    @DecimalMin(value = "0.01", message = "Сумма должна быть больше 0")
    private Double amount;
}

