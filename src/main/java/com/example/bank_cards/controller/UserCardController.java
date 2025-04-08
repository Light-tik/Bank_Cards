package com.example.bank_cards.controller;

import com.example.bank_cards.constants.MyConstants;
import com.example.bank_cards.dto.response.*;
import com.example.bank_cards.service.CardService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/cards")
@RequiredArgsConstructor
@Validated
public class UserCardController {

    private final CardService cardService;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<List<CardResponse>> getMyCards() {
        return ResponseEntity.ok(cardService.getUserCards());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<CardResponse> getCardById(
            @PathVariable UUID id) {
        return ResponseEntity.ok(cardService.getCard(id));
    }

    @GetMapping("/page")
    public ResponseEntity<CustomResponse<PageableResponse<CardResponse>>> getAllCardsByPage(
            @RequestParam @Positive Integer page,
            @RequestParam @Positive Integer perPage) {
        return ResponseEntity.ok(new CustomResponse<>(cardService.getCardsOnPage(page, perPage)));
    }

    @GetMapping("/limit/{id}")
    public ResponseEntity<LimitResponse> getCardLimitById(
            @PathVariable UUID id) {
        return ResponseEntity.ok(cardService.getLimit(id));
    }

    @PatchMapping("/{id}/block-request")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<CustomResponse<String>> requestCardBlock(
            @PathVariable @NotNull UUID id) {
        cardService.requestBlockCard(id);
        return ResponseEntity.ok(new CustomResponse<>(MyConstants.SUCCESS_MESSAGE));
    }
}


