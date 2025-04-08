package com.example.bank_cards.dto.response;

import com.example.bank_cards.enums.StatusCard;
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
public class CardResponse {

    private UUID id;

    private String maskedNumber;

    private UUID ownerId;

    private YearMonth expiryDate;

    private StatusCard status;

    private Double balance;
}

