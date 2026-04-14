package io.github.winroot33.authenticationservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO для успешного ответа после подтверждения почты
 *
 * @author Mikhail Vasiliev (winroot123@gmail.com)
 */
@Schema(description = "Ответ после подтверждения почты")
public record EmailConfirmationResponse(
        @Schema(description = "Сообщение для пользователя", example = "Почта успешно подтверждена")
        String message
) {
}
