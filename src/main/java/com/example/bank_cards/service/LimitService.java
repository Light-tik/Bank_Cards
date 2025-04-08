package com.example.bank_cards.service;

import com.example.bank_cards.dto.request.LimitRequest;
import com.example.bank_cards.dto.response.LimitResponse;
import com.example.bank_cards.entity.CardEntity;

import java.util.UUID;

public interface LimitService {

    LimitResponse setMonthlyLimit(LimitRequest request, CardEntity cardEntity);

    boolean checkLimit(UUID cardId, Double amount);

    void sumAmount(Double amount, UUID cardId);

    LimitResponse getLimit(UUID cardId);
}
