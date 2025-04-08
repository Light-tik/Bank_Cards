package com.example.bank_cards.dto.response;

import com.example.bank_cards.enums.Roles;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginUserResponse {

    @Schema(description = "Email пользователя", example = "user@example.com")
    private String email;

    @Schema(description = "UUID пользователя", example = "4b8d8e4e-79c9-41b2-9c93-e6554ad87001")
    private UUID id;

    @Schema(description = "Имя пользователя", example = "Иван")
    private String name;

    @Schema(description = "Роль пользователя", example = "ROLE_USER")
    private Roles role;

    @Schema(description = "JWT токен для авторизации", example = "eyJhbGciOiJIUz...")
    private String token;
}

