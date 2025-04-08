package com.example.bank_cards.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Статус карты")
public enum StatusCard {
    ACTIVE,
    BLOCKED,
    WAITING_FOR_LOCK,
    EXPIRED
}
