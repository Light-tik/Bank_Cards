package com.example.bank_cards.controller;

import com.example.bank_cards.dto.request.SignInRequest;
import com.example.bank_cards.dto.response.BaseResponse;
import com.example.bank_cards.dto.response.CustomResponse;
import com.example.bank_cards.dto.response.LoginUserResponse;
import com.example.bank_cards.dto.request.SignUpRequest;
import com.example.bank_cards.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
@Validated
@Tag(name = "Аутентификация", description = "Эндпоинты для регистрации и входа в систему")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Регистрация пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно зарегистрирован"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации или пользователь уже существует", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    })
    @PostMapping("/sign-up")
    public ResponseEntity<CustomResponse<LoginUserResponse>> registration(
            @Valid @RequestBody SignUpRequest user) {
        return ResponseEntity.ok(new CustomResponse<>(authService.registerUser(user)));
    }

    @Operation(summary = "Авторизация пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешная авторизация"),
            @ApiResponse(responseCode = "401", description = "Неверный логин или пароль", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    })
    @PostMapping("/sign-in")
    public ResponseEntity<CustomResponse<LoginUserResponse>> login(
            @Valid @RequestBody SignInRequest user) {
        return ResponseEntity.ok(new CustomResponse<>(authService.loginUser(user)));
    }
}
