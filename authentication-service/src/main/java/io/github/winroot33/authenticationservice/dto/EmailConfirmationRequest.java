package io.github.winroot33.authenticationservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO для запроса подтверждения почты
 *
 * @author Mikhail Vasiliev (winroot123@gmail.com)
 */
@Schema(description = "Запрос для подтверждения почты пользователя")
public record EmailConfirmationRequest(
        @Schema(description = "Адрес электронной почты", example = "admin@example.com",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @Size(min = 5, max = 255, message = "Адрес электронной почты должен содержать от 5 до 255 символов")
        @NotBlank(message = "Адрес электронной почты не может быть пустыми")
        @Email(message = "Email адрес должен быть в формате user@example.com")
        String email,

        @Schema(description = "Код подтверждения из письма", example = "123456",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Код подтверждения обязателен")
        @Size(min = 6, max = 6, message = "Код должен содержать 6 символов")
        String confirmationCode
) {
}
