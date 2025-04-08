package com.example.bank_cards.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LimitResponse {

    private UUID cardId;

    private Double monthlyLimit;

    private Double usedThisMonth;
}

