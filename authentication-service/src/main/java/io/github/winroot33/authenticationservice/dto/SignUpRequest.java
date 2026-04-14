package io.github.winroot33.authenticationservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


/**
 * DTO для запроса регистрации пользователя
 *
 * @author Mikhail Vasiliev (winroot123@gmail.com)
 */
@Schema(description = "Запрос на регистрацию")
public record SignUpRequest(
        @Schema(description = "Адрес электронной почты", example = "admin@example.com",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @Size(min = 5, max = 255, message = "Адрес электронной почты должен содержать от 5 до 255 символов")
        @NotBlank(message = "Адрес электронной почты не может быть пустыми")
        @Email(message = "Email адрес должен быть в формате user@example.com")
        String email,

        @Schema(description = "Пароль", example = "password", requiredMode = Schema.RequiredMode.REQUIRED)
        @Size(min = 8, max = 255, message = "Длина пароля должна быть от 8 до 255 символов")
        @NotBlank(message = "Пароль не может быть пустым")
        String password
) {
}
