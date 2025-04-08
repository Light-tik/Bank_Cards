package com.example.bank_cards.service;

import com.example.bank_cards.dto.request.CardRequest;
import com.example.bank_cards.dto.request.LimitRequest;
import com.example.bank_cards.dto.response.CardResponse;
import com.example.bank_cards.dto.response.LimitResponse;
import com.example.bank_cards.entity.CardEntity;
import com.example.bank_cards.enums.StatusCard;
import com.example.bank_cards.dto.response.PageableResponse;

import java.util.List;
import java.util.UUID;

public interface CardService {

    CardResponse createCard(CardRequest cardRequest);

    void deleteCard(UUID cardId);

    CardResponse changeCardStatus(UUID cardId, StatusCard newStatus);

    List<CardResponse> getUserCards();

    CardResponse getCard(UUID cardId);

    PageableResponse<CardResponse> getCardsOnPage(Integer page, Integer perPage);

    PageableResponse<CardResponse> searchCards(UUID ownerId, StatusCard status, String maskedNumber, Integer page, Integer perPage);

    void deleteAllCard();

    LimitResponse setMonthlyLimit(LimitRequest request);

    void requestBlockCard(UUID id);

    CardEntity getCardEntity(UUID cardId);

    LimitResponse getLimit(UUID cardId);
}
