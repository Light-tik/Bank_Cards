package com.example.bank_cards.controller;

import com.example.bank_cards.dto.request.SignInRequest;
import com.example.bank_cards.dto.response.CustomResponse;
import com.example.bank_cards.dto.response.LoginUserResponse;
import com.example.bank_cards.dto.request.SignUpRequest;
import com.example.bank_cards.service.AuthService;
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
public class AuthController {

    private final AuthService authService;

    @PostMapping("/sign-up")
    public ResponseEntity<CustomResponse<LoginUserResponse>> registration(
            @Valid @RequestBody SignUpRequest user) {
        return ResponseEntity.ok(new CustomResponse<>(authService.registerUser(user)));
    }

    @PostMapping("/sign-in")
    public ResponseEntity<CustomResponse<LoginUserResponse>> login(
            @Valid @RequestBody SignInRequest user) {
        return ResponseEntity.ok(new CustomResponse<>(authService.loginUser(user)));
    }
}

