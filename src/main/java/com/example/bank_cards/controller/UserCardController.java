package com.example.bank_cards.controller;

import com.example.bank_cards.constants.MyConstants;
import com.example.bank_cards.dto.response.*;
import com.example.bank_cards.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("api/user/cards")
@RequiredArgsConstructor
@Validated
@Tag(name = "Управление картами пользователя", description = "Методы для работы с картами пользователя")
public class UserCardController {

    private final CardService cardService;

    @Operation(summary = "Получить список всех карт текущего пользователя", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список карт успешно получен"),
            @ApiResponse(responseCode = "403", description = "Не авторизован", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<List<CardResponse>> getMyCards() {
        return ResponseEntity.ok(cardService.getUserCards());
    }

    @Operation(summary = "Получить информацию о конкретной карте по ID", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Информация о карте успешно получена"),
            @ApiResponse(responseCode = "404", description = "Карта не найдена", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<CardResponse> getCardById(
            @Parameter(description = "ID карты", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(cardService.getCard(id));
    }

    @Operation(summary = "Получить список всех карт с пагинацией")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Страница с картами"),
            @ApiResponse(responseCode = "404", description = "Некорректный запрос", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))})
    @GetMapping("/page")
    public ResponseEntity<CustomResponse<PageableResponse<CardResponse>>> getAllCardsByPage(
            @Parameter(description = "Номер страницы (начиная с 1)", required = true)
            @RequestParam @Positive Integer page,

            @Parameter(description = "Количество строк на странице", required = true)
            @RequestParam @Positive Integer perPage) {
        return ResponseEntity.ok(new CustomResponse<>(cardService.getCardsOnPage(page, perPage)));
    }

    @Operation(summary = "Получить лимит по карте по её ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Лимит успешно получен"),
            @ApiResponse(responseCode = "404", description = "Карта или лимит не найдены", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping("/limit/{id}")
    public ResponseEntity<LimitResponse> getCardLimitById(
            @Parameter(description = "ID карты", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(cardService.getLimit(id));
    }

    @Operation(summary = "Отправить запрос на блокировку карты", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запрос на блокировку успешно отправлен"),
            @ApiResponse(responseCode = "400", description = "Невалидный запрос", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    })
    @PatchMapping("/{id}/block-request")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<CustomResponse<String>> requestCardBlock(
            @Parameter(description = "ID карты", required = true)
            @PathVariable @NotNull UUID id) {
        cardService.requestBlockCard(id);
        return ResponseEntity.ok(new CustomResponse<>(MyConstants.SUCCESS_MESSAGE));
    }
}


