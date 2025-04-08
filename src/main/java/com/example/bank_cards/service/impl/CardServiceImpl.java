package com.example.bank_cards.service.impl;

import com.example.bank_cards.dto.request.CardRequest;
import com.example.bank_cards.dto.request.LimitRequest;
import com.example.bank_cards.dto.response.CardResponse;
import com.example.bank_cards.dto.response.LimitResponse;
import com.example.bank_cards.dto.response.PageableResponse;
import com.example.bank_cards.entity.CardEntity;
import com.example.bank_cards.entity.UserEntity;
import com.example.bank_cards.enums.StatusCard;
import com.example.bank_cards.mappers.CardMapper;
import com.example.bank_cards.repositories.CardRepository;
import com.example.bank_cards.security.SecurityContextFacade;
import com.example.bank_cards.service.*;
import com.example.bank_cards.validation.CustomException;
import com.example.bank_cards.validation.ErrorCodes;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final SecurityContextFacade securityContextFacade;
    private final CardMapper cardMapper;
    private final CardEncryptionService cardEncryptionService;
    private final LimitService limitService;
    private final TransactionService transactionService;
    private final UserService userService;

    @Override
    @Transactional
    public CardResponse createCard(CardRequest cardRequest) {
        UserEntity user = userService.getUserEntity(cardRequest.getOwnerId());

        CardEntity cardEntity = cardMapper.toEntity(cardRequest, cardEncryptionService);
        cardEntity.setOwner(user);
        cardRepository.save(cardEntity);

        return cardMapper.toResponse(cardEntity, cardEncryptionService);
    }

    @Override
    @Transactional
    public void deleteCard(UUID cardId) {
        cardRepository.deleteById(cardId);
        transactionService.deleteAllByCard();
    }

    @Override
    @Transactional
    public CardResponse changeCardStatus(UUID cardId, StatusCard newStatus) {
        CardEntity cardEntity = getCardEntity(cardId);
        cardEntity.setStatus(newStatus);
        cardRepository.save(cardEntity);
        return cardMapper.toResponse(cardEntity, cardEncryptionService);
    }

    @Override
    @Transactional
    public void deleteAllCard() {
        cardRepository.deleteAll(cardRepository.findCardsWithoutUser());
        transactionService.deleteAllByCard();
    }

    @Override
    public LimitResponse setMonthlyLimit(LimitRequest request) {
        CardEntity entity = getCardEntity(request.getCardId());
        return limitService.setMonthlyLimit(request, entity);
    }

    @Override
    public void requestBlockCard(UUID cardId) {
        CardEntity cardEntity = cardRepository.findById(cardId).orElseThrow(() -> new CustomException(ErrorCodes.CARD_NOT_FOUND));
        cardEntity.setStatus(StatusCard.WAITING_FOR_LOCK);
    }

    @Override
    public List<CardResponse> getUserCards() {
        List<CardEntity> cards = cardRepository.findByOwnerId(securityContextFacade.getCurrentUserId());
        return cardMapper.toListResponse(cards, cardEncryptionService);
    }

    @Override
    public CardResponse getCard(UUID cardId) {
        CardEntity card = getCardEntity(cardId);
        return cardMapper.toResponse(card, cardEncryptionService);
    }

    @Override
    public PageableResponse<CardResponse> getCardsOnPage(Integer page, Integer perPage) {
        Pageable pageable = PageRequest.of(page - 1, perPage);
        Page<CardEntity> cardPage = cardRepository.findAll(pageable);
        List<CardResponse> responses = cardMapper.toListResponse(cardPage.getContent(), cardEncryptionService);
        return new PageableResponse<>(responses, cardPage.getTotalElements());
    }

    @Override
    public PageableResponse<CardResponse> searchCards(UUID ownerId, StatusCard status, String maskedNumber, Integer page, Integer perPage) {
        Pageable pageable = PageRequest.of(page - 1, perPage);
        Page<CardEntity> cardPage = cardRepository.searchCards(ownerId, status, maskedNumber, pageable);
        List<CardResponse> responses = cardMapper.toListResponse(cardPage.getContent(), cardEncryptionService);
        return new PageableResponse<>(responses, cardPage.getTotalElements());
    }

    @Override
    public CardEntity getCardEntity(UUID cardId) {
        return cardRepository.findById(cardId).orElseThrow(() -> new CustomException(ErrorCodes.CARD_NOT_FOUND));
    }

    @Override
    public LimitResponse getLimit(UUID cardId) {
        getCardEntity(cardId);
        return limitService.getLimit(cardId);
    }
}
