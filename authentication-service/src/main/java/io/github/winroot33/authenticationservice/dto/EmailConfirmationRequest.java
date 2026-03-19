package io.github.winroot33.authenticationservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Запрос для подтверждения почты пользователя")
public record EmailConfirmationRequest(
        @Schema(description = "ID пользователя", example = "123", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "ID пользователя обязателен")
        Long userId,

        @Schema(description = "Код подтверждения из письма", example = "123456",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Код подтверждения обязателен")
        @Size(min = 6, max = 6, message = "Код должен содержать 6 символов")
        String confirmationCode
) {
}
