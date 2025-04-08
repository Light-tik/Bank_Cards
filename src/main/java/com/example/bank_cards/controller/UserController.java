package com.example.bank_cards.controller;

import com.example.bank_cards.dto.request.SignUpRequest;
import com.example.bank_cards.dto.response.BaseResponse;
import com.example.bank_cards.dto.response.CustomResponse;
import com.example.bank_cards.dto.response.PublicUserView;
import com.example.bank_cards.service.UserCardService;
import com.example.bank_cards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/admin/users")
@Tag(name = "Пользователи", description = "Управление пользователями и их информацией")
public class UserController {

    private final UserService userService;

    private final UserCardService userCardService;

    @Operation(summary = "Получить список всех пользователей (для администратора)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список пользователей успешно получен"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping
    public ResponseEntity<CustomResponse<List<PublicUserView>>> getAll() {
        return ResponseEntity.ok().body(new CustomResponse<>(userService.getListUsers()));
    }

    @Operation(summary = "Получить информацию о пользователе по ID (для администратора)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Информация о пользователе получена"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping("{id}")
    public ResponseEntity<CustomResponse<PublicUserView>> getUserInfoById(
            @Parameter(description = "ID пользователя", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(new CustomResponse<>(userService.getUser(id)));
    }

    @Operation(summary = "Получить информацию о текущем пользователе")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Информация о пользователе получена"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping("/info")
    public ResponseEntity<CustomResponse<PublicUserView>> getUserInfo() {
        return ResponseEntity.ok(new CustomResponse<>(userService.getUserInfo()));
    }

    @Operation(summary = "Удалить пользователя по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно удалён"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse> deleteUser(
            @Parameter(description = "ID пользователя", required = true)
            @PathVariable UUID id) {
        userCardService.deleteUserWithCards(id);
        return ResponseEntity.ok(new BaseResponse(1));
    }

    @Operation(summary = "Обновить информацию о пользователе по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Информация о пользователе обновлена"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<CustomResponse<PublicUserView>> replaceUser(
            @Parameter(description = "ID пользователя", required = true)
            @PathVariable UUID id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Новые данные пользователя",
                    required = true,
                    content = @Content(schema = @Schema(implementation = SignUpRequest.class))
            )
            @Valid @RequestBody SignUpRequest request) {
        return ResponseEntity.ok(new CustomResponse<>(userService.putUser(id, request)));
    }
}

