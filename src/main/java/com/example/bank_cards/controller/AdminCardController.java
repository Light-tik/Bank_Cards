package com.example.bank_cards.controller;

import com.example.bank_cards.dto.request.CardRequest;
import com.example.bank_cards.dto.request.LimitRequest;
import com.example.bank_cards.dto.response.*;
import com.example.bank_cards.enums.StatusCard;
import com.example.bank_cards.service.CardService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/admin/cards")
@RequiredArgsConstructor
@Validated
public class AdminCardController {

    private final CardService cardService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<CardResponse> createCard(
            @Valid @RequestBody CardRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cardService.createCard(request));
    }

    @GetMapping
    public ResponseEntity<CustomResponse<PageableResponse<CardResponse>>> getAllCards(
           @RequestParam @Positive Integer page,
           @RequestParam @Positive Integer perPage) {
        return ResponseEntity.ok(new CustomResponse<>(cardService.getCardsOnPage(page, perPage)));
    }

    @GetMapping("/search")
    public ResponseEntity<CustomResponse<PageableResponse<CardResponse>>> searchCards(
            @RequestParam(required = false) UUID ownerId,
            @RequestParam(required = false) StatusCard status,
            @RequestParam(required = false) String maskedNumber,
            @RequestParam @Positive Integer page,
            @RequestParam @Positive Integer perPage) {
        return ResponseEntity.ok(new CustomResponse<>(cardService.searchCards(ownerId, status, maskedNumber, page, perPage)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<CardResponse> getCardById(
            @PathVariable("id") UUID cardId) {
        return ResponseEntity.ok(cardService.getCard(cardId));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<CardResponse> updateStatus(
            @PathVariable UUID id,
            @RequestParam StatusCard status) {
        return ResponseEntity.ok(cardService.changeCardStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse> deleteCard(
            @PathVariable UUID id) {
        cardService.deleteCard(id);
        return ResponseEntity.ok(new BaseResponse(1));
    }

    @PostMapping("/limit")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<LimitResponse> setMonthlyLimit(
            @Valid @RequestBody LimitRequest request) {
        return ResponseEntity.ok(cardService.setMonthlyLimit(request));
    }
}
