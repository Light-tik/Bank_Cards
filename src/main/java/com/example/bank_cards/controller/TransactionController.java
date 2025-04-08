package com.example.bank_cards.controller;

import com.example.bank_cards.dto.request.TransactionRequest;
import com.example.bank_cards.dto.response.BaseResponse;
import com.example.bank_cards.dto.response.CustomResponse;
import com.example.bank_cards.dto.response.PageableResponse;
import com.example.bank_cards.dto.response.TransactionResponse;
import com.example.bank_cards.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Управление транзакциями", description = "Операции перевода, пополнения и списания с карт")
public class TransactionController {

    private final TransactionService transactionService;

    @Operation(summary = "Перевод между картами", description = "Позволяет перевести средства между двумя картами")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный перевод"),
            @ApiResponse(responseCode = "400", description = "Неверные данные запроса", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "401", description = "Неавторизован", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
    })
    @PostMapping("/transfer")
    public ResponseEntity<CustomResponse<TransactionResponse>> transferBetweenCards(
            @Valid @RequestBody TransactionRequest request) {
        return ResponseEntity.ok(new CustomResponse<>(transactionService.transfer(request)));
    }

    @Operation(summary = "Пополнение карты", description = "Добавляет средства на карту")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Карта пополнена"),
            @ApiResponse(responseCode = "404", description = "Карта не найдена", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    })
    @PostMapping("/replenish")
    public ResponseEntity<CustomResponse<TransactionResponse>> withdraw(
            @Parameter(description = "ID карты получателя", required = true)
            @RequestParam UUID toCard,
            @Parameter(description = "Сумма пополнения", required = true, example = "100.00")
            @RequestParam @DecimalMin("0.01") Double amount) {
        return ResponseEntity.ok(new CustomResponse<>(transactionService.replenish(toCard, amount)));
    }

    @Operation(summary = "Списание с карты", description = "Списывает средства с карты")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Списание денежный средств успешно"),
            @ApiResponse(responseCode = "404", description = "Карта не найдена", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    })
    @PostMapping("/debit")
    public ResponseEntity<CustomResponse<TransactionResponse>> debit(
            @Parameter(description = "ID карты списания", required = true)
            @RequestParam UUID fromCard,
            @Parameter(description = "Сумма списания", required = true, example = "50.00")
            @RequestParam @DecimalMin("0.01") Double amount) {
        return ResponseEntity.ok(new CustomResponse<>(transactionService.debit(fromCard, amount)));
    }

    @Operation(summary = "Получить транзакции по карте", description = "Возвращает список всех транзакций для карты")
    @Parameter(name = "id", description = "ID карты", required = true)
    @GetMapping("/{id}")
    public ResponseEntity<CustomResponse<List<TransactionResponse>>> getTransactions(@PathVariable UUID id) {
        return ResponseEntity.ok(new CustomResponse<>(transactionService.getTransactionsByCard(id)));
    }

    @Operation(summary = "Получить список всех транзакций с пагинацией")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список транзакций успешно получен",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageableResponse.class)))
    })
    @GetMapping("/page")
    public ResponseEntity<CustomResponse<PageableResponse<TransactionResponse>>> getAllTransactions(
            @Parameter(description = "Номер страницы (начиная с 1)", required = true)
            @RequestParam @Positive Integer page,

            @Parameter(description = "Количество строк на странице", required = true)
            @RequestParam @Positive Integer perPage) {
        return ResponseEntity.ok(new CustomResponse<>(transactionService.getTransactionsOnPage(page, perPage)));
    }

    @Operation(summary = "Поиск транзакций по фильтрам")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Транзакции успешно найдены"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping("/filter")
    public ResponseEntity<CustomResponse<PageableResponse<TransactionResponse>>> filterTransactions(
            @Parameter(description = "ID карты")
            @RequestParam(required = false) UUID cardId,

            @Parameter(description = "Сумма")
            @RequestParam(required = false) Double amount,

            @Parameter(description = "Начало временного периода")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,

            @Parameter(description = "Конец временного периода операции")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,

            @Parameter(description = "Количество строк на странице", required = true)
            @RequestParam @Positive Integer page,

            @Parameter(description = "Количество строк на странице", required = true)
            @RequestParam @Positive Integer perPage) {
        return ResponseEntity.ok(new CustomResponse<>(transactionService
                .filterTransactions(cardId, amount, fromDate, toDate, page, perPage)));
    }
}
