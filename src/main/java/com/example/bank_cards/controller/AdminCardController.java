package com.example.bank_cards.controller;

import com.example.bank_cards.dto.request.CardRequest;
import com.example.bank_cards.dto.request.LimitRequest;
import com.example.bank_cards.dto.response.*;
import com.example.bank_cards.enums.StatusCard;
import com.example.bank_cards.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Управление картами (админ)", description = "Операции по управлению картами, доступные только администраторам")
public class AdminCardController {

    private final CardService cardService;

    @Operation(summary = "Создать карту")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Карта успешно создана"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации данных", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
    })
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<CardResponse> createCard(
            @Valid @RequestBody CardRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cardService.createCard(request));
    }

    @Operation(summary = "Получить список всех карт с пагинацией")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список карт успешно получен"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации данных", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping("/page")
    public ResponseEntity<CustomResponse<PageableResponse<CardResponse>>> getAllCards(
            @Parameter(description = "Номер страницы (от 1)") @RequestParam @Positive Integer page,
            @Parameter(description = "Количество элементов на странице") @RequestParam @Positive Integer perPage) {
        return ResponseEntity.ok(new CustomResponse<>(cardService.getCardsOnPage(page, perPage)));
    }

    @Operation(summary = "Поиск карт по фильтрам")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Карты успешно найдены"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации данных", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping("/search")
    public ResponseEntity<CustomResponse<PageableResponse<CardResponse>>> searchCards(
            @Parameter(description = "ID владельца карты") @RequestParam(required = false) UUID ownerId,
            @Parameter(description = "Статус карты") @RequestParam(required = false) StatusCard status,
            @Parameter(description = "Маскированный номер карты") @RequestParam(required = false) String maskedNumber,
            @RequestParam @Positive Integer page,
            @RequestParam @Positive Integer perPage) {
        return ResponseEntity.ok(new CustomResponse<>(cardService.searchCards(ownerId, status, maskedNumber, page, perPage)));
    }

    @Operation(summary = "Получить карту по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Карта найдена"),
            @ApiResponse(responseCode = "404", description = "Карта не найдена", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<CardResponse> getCardById(
            @Parameter(description = "ID карты", required = true)
            @PathVariable("id") UUID cardId) {
        return ResponseEntity.ok(cardService.getCard(cardId));
    }

    @Operation(summary = "Обновить статус карты")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Статус карты обновлен"),
            @ApiResponse(responseCode = "404", description = "Карта не найдена", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
    })
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<CardResponse> updateStatus(
            @Parameter(description = "ID карты", required = true) @PathVariable UUID id,
            @Parameter(description = "Новый статус", required = true) @RequestParam StatusCard status) {
        return ResponseEntity.ok(cardService.changeCardStatus(id, status));
    }

    @Operation(summary = "Удалить карту по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Карта удалена"),
            @ApiResponse(responseCode = "404", description = "Карта не найдена", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse> deleteCard(
            @Parameter(description = "ID карты", required = true) @PathVariable UUID id) {
        cardService.deleteCard(id);
        return ResponseEntity.ok(new BaseResponse(1));
    }

    @Operation(summary = "Установить месячный лимит по карте")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Лимит установлен"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    })
    @PostMapping("/limit")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<LimitResponse> setMonthlyLimit(
            @Valid @RequestBody LimitRequest request) {
        return ResponseEntity.ok(cardService.setMonthlyLimit(request));
    }
}
