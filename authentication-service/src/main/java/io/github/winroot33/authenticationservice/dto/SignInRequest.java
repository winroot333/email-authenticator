package io.github.winroot33.authenticationservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Запрос на аутентификацию")
public record SignInRequest(

        @Schema(description = "Почта пользователя", example = "admin@example.com",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @Size(min = 5, max = 255, message = "Почта пользователя должна содержать от 5 до 255 символов")
        @Email(message = "Email адрес должен быть в формате user@example.com")
        @NotBlank(message = "Почта пользователя не может быть пустыми")
        String email,

        @Schema(description = "Пароль", example = "password", requiredMode = Schema.RequiredMode.REQUIRED)
        @Size(min = 8, max = 255, message = "Длина пароля должна быть от 8 до 255 символов")
        @NotBlank(message = "Пароль не может быть пустыми")
        String password
) {}
