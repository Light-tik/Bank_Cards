package com.example.bank_cards.controller;

import com.example.bank_cards.dto.request.TransactionRequest;
import com.example.bank_cards.dto.response.CustomResponse;
import com.example.bank_cards.dto.response.PageableResponse;
import com.example.bank_cards.dto.response.TransactionResponse;
import com.example.bank_cards.service.TransactionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("api/transactional")
@RequiredArgsConstructor
@Validated
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/transfer")
    public ResponseEntity<CustomResponse<TransactionResponse>> transferBetweenCards(
            @Valid @RequestBody TransactionRequest request) {
        return ResponseEntity.ok(new CustomResponse<>(transactionService.transfer(request)));
    }

    @PostMapping("/replenish")
    public ResponseEntity<CustomResponse<TransactionResponse>> withdraw(
            @RequestParam UUID toCard,
            @RequestParam @DecimalMin("0.01") Double amount) {
        return ResponseEntity.ok(new CustomResponse<>(transactionService.replenish(toCard, amount)));
    }

    @PostMapping("/debit")
    public ResponseEntity<CustomResponse<TransactionResponse>> debit(
            @RequestParam UUID fromCard,
            @RequestParam @DecimalMin("0.01") Double amount) {
        return ResponseEntity.ok(new CustomResponse<>(transactionService.debit(fromCard, amount)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomResponse<List<TransactionResponse>>> getTransactions(@PathVariable UUID id) {
        return ResponseEntity.ok(new CustomResponse<>(transactionService.getTransactionsByCard(id)));
    }

    @GetMapping("/page")
    public ResponseEntity<CustomResponse<PageableResponse<TransactionResponse>>> getAllTransactions(
            @RequestParam @Positive Integer page,
            @RequestParam @Positive Integer perPage) {
        return ResponseEntity.ok(new CustomResponse<>(transactionService.getTransactionsOnPage(page, perPage)));
    }

    @GetMapping("/filter")
    public ResponseEntity<CustomResponse<PageableResponse<TransactionResponse>>> filterTransactions(
            @RequestParam(required = false) UUID cardId,
            @RequestParam(required = false) Double amount,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
            @RequestParam @Positive Integer page,
            @RequestParam @Positive Integer perPage) {
        return ResponseEntity.ok(new CustomResponse<>(transactionService
                .filterTransactions(cardId, amount, fromDate, toDate, page, perPage)));
    }
}
