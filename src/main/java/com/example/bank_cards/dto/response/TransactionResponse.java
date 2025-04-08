package com.example.bank_cards.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Ответ с информацией по транзакции")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponse {

    @Schema(description = "ID транзакции", example = "a0987667-89ab-cdef-0123-45678fkkk890")
    private UUID id;

    @Schema(description = "Сумма денежных средств", example = "10000.0")
    private Double amount;

    @Schema(description = "ID транзакции", example = "")
    private LocalDateTime timestamp;

    @Schema(description = "ID транзакции", example = "a1234567-89ab-cdef-0123-456789abcdef")
    private UUID cardId;
}
