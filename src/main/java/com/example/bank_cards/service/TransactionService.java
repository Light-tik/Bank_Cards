package com.example.bank_cards.service;

import com.example.bank_cards.dto.request.TransactionRequest;
import com.example.bank_cards.dto.response.PageableResponse;
import com.example.bank_cards.dto.response.TransactionResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TransactionService {

    PageableResponse<TransactionResponse> getTransactionsOnPage(Integer page, Integer perPage);

    PageableResponse<TransactionResponse> filterTransactions(UUID cardId, Double amount,
                                                             LocalDateTime fromDate, LocalDateTime toDate,
                                                             Integer page, Integer perPage);

    TransactionResponse debit(UUID fromCardId, Double amount);

    TransactionResponse transfer(TransactionRequest request);

    TransactionResponse replenish(UUID toCardId, Double amount);

    List<TransactionResponse> getTransactionsByCard(UUID cardId);

    void deleteAllByCard();
}
