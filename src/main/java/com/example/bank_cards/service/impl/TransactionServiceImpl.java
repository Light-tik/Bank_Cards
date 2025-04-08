package com.example.bank_cards.service.impl;

import com.example.bank_cards.dto.request.TransactionRequest;
import com.example.bank_cards.dto.response.PageableResponse;
import com.example.bank_cards.dto.response.TransactionResponse;
import com.example.bank_cards.entity.CardEntity;
import com.example.bank_cards.entity.TransactionEntity;
import com.example.bank_cards.enums.Roles;
import com.example.bank_cards.enums.TransactionType;
import com.example.bank_cards.mappers.TransactionMapper;
import com.example.bank_cards.repositories.CardRepository;
import com.example.bank_cards.repositories.TransactionRepository;
import com.example.bank_cards.security.SecurityContextFacade;
import com.example.bank_cards.service.LimitService;
import com.example.bank_cards.service.TransactionService;
import com.example.bank_cards.validation.CustomException;
import com.example.bank_cards.validation.ErrorCodes;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private final CardRepository cardRepository;
    private final TransactionRepository transactionRepository;
    private final SecurityContextFacade securityContextFacade;
    private final LimitService limitService;
    private final TransactionMapper transactionMapper;

    @Override
    public TransactionResponse transfer(TransactionRequest request) {
        Double amount = request.getAmount();
        if (amount <= 0) {
            throw new CustomException(ErrorCodes.INVALID_AMOUNT);
        }

        UUID userId = securityContextFacade.getCurrentUserId();

        CardEntity fromCard = getCardById(request.getFromCardId());
        CardEntity toCard = getCardById(request.getToCardId());

        if (!fromCard.getOwner().getId().equals(userId) || !toCard.getOwner().getId().equals(userId)) {
            throw new CustomException(ErrorCodes.NO_RIGHTS);
        }

        if (fromCard.getBalance() < amount) {
            throw new CustomException(ErrorCodes.INSUFFICIENT_FUNDS);
        }

        if (!limitService.checkLimit(fromCard.getId(), amount)) {
            throw new CustomException(ErrorCodes.LIMIT_EXCEEDED);
        }

        fromCard.setBalance(fromCard.getBalance() - amount);
        toCard.setBalance(toCard.getBalance() + amount);

        cardRepository.save(fromCard);
        cardRepository.save(toCard);

        TransactionEntity transaction = transactionMapper.toEntity(request, fromCard, TransactionType.DEBIT);

        transactionRepository.save(transaction);
        transactionRepository.save(transactionMapper.toEntity(request, toCard, TransactionType.CREDIT));
        limitService.sumAmount(amount, fromCard.getId());
        limitService.sumAmount(amount, toCard.getId());
        return transactionMapper.toResponse(transaction);
    }

    @Override
    public TransactionResponse replenish(UUID toCardId, Double amount) {
        if (amount <= 0) {
            throw new CustomException(ErrorCodes.INVALID_AMOUNT);
        }

        CardEntity card = getCardById(toCardId);
        checkAccess(card);

        if (!limitService.checkLimit(card.getId(), amount)) {
            throw new CustomException(ErrorCodes.LIMIT_EXCEEDED);
        }

        card.setBalance(card.getBalance() + amount);
        cardRepository.save(card);

        TransactionRequest req = new TransactionRequest();
        req.setAmount(amount);

        TransactionEntity transaction = transactionMapper.toEntity(req, card, TransactionType.CREDIT);
        transactionRepository.save(transaction);
        limitService.sumAmount(amount, toCardId);
        return transactionMapper.toResponse(transaction);
    }

    @Override
    public List<TransactionResponse> getTransactionsByCard(UUID cardId) {
        List<TransactionEntity> transactions = transactionRepository.findTransactionEntitiesByCardId(cardId);
        return transactionMapper.toTransactionResponseList(transactions);
    }

    @Override
    public PageableResponse<TransactionResponse> getTransactionsOnPage(Integer page, Integer perPage) {
        Pageable pageable = PageRequest.of(page - 1, perPage);
        Page<TransactionEntity> transactionPage = transactionRepository.findAll(pageable);
        List<TransactionResponse> responses = transactionMapper.toTransactionResponseList(transactionPage.getContent());
        return new PageableResponse<>(responses, transactionPage.getTotalElements());
    }

    @Override
    public PageableResponse<TransactionResponse> filterTransactions(UUID cardId, Double amount,
                                                                    LocalDateTime fromDate, LocalDateTime toDate,
                                                                    Integer page, Integer perPage) {
        Pageable pageable = PageRequest.of(page - 1, perPage);
        Page<TransactionEntity> transactionPage = transactionRepository
                .filterTransactions(cardId, amount, fromDate, toDate, pageable);
        List<TransactionResponse> responses = transactionMapper.toTransactionResponseList(transactionPage.getContent());
        return new PageableResponse<>(responses, transactionPage.getTotalElements());
    }

    @Override
    public TransactionResponse debit(UUID fromCardId, Double amount) {
        if (amount <= 0) {
            throw new CustomException(ErrorCodes.INVALID_AMOUNT);
        }

        CardEntity card = getCardById(fromCardId);
        checkAccess(card);

        if (card.getBalance() < amount) {
            throw new CustomException(ErrorCodes.INSUFFICIENT_FUNDS);
        }

        if (!limitService.checkLimit(fromCardId, amount)) {
            throw new CustomException(ErrorCodes.LIMIT_EXCEEDED);
        }

        card.setBalance(card.getBalance() - amount);
        cardRepository.save(card);

        TransactionRequest req = new TransactionRequest();
        req.setAmount(amount);

        TransactionEntity transaction = transactionMapper.toEntity(req, card, TransactionType.DEBIT);
        transactionRepository.save(transaction);
        limitService.sumAmount(amount, fromCardId);
         return transactionMapper.toResponse(transaction);
    }

    private CardEntity getCardById(UUID cardId) {
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new CustomException(ErrorCodes.CARD_NOT_FOUND));
    }

    @Override
    public void deleteAllByCard(){
        transactionRepository.deleteAll(transactionRepository.findTransactionEntitiesWithoutCards());
    }

    private void checkAccess(CardEntity card) {
        UUID userId = securityContextFacade.getCurrentUserId();
        Roles role = securityContextFacade.getCurrentUserRole();
        if (!card.getOwner().getId().equals(userId) && role != Roles.ROLE_ADMIN) {
            throw new CustomException(ErrorCodes.NO_RIGHTS);
        }
    }
}
