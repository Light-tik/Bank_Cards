package com.example.bank_cards.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Тип операции")
public enum TransactionType {
    DEBIT,
    CREDIT
}
