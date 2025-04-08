package com.example.bank_cards.dto.request;

import com.example.bank_cards.constants.ValidationConstants;
import com.example.bank_cards.enums.Roles;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "Запрос на регистрацию нового пользователя")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {

    @Schema(description = "Email", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = ValidationConstants.USER_EMAIL_NOT_NULL)
    @Pattern(message = ValidationConstants.USER_EMAIL_NOT_VALID, regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")
    @Size(min = 3, max = 100, message = ValidationConstants.EMAIL_SIZE_NOT_VALID)
    private String email;

    @Schema(description = "Имя пользователя", example = "Иван", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(min = 3, max = 25, message = ValidationConstants.USERNAME_SIZE_NOT_VALID)
    @NotBlank(message = ValidationConstants.USER_NAME_NOT_NULL)
    private String name;

    @Schema(description = "Пароль", example = "password123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = ValidationConstants.PASSWORD_NOT_NULL)
    private String password;

    @Schema(description = "Роль пользователя", example = "ROLE_USER")
    private Roles role;
}
