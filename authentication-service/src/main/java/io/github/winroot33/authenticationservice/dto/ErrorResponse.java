package io.github.winroot33.authenticationservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Объект ответа при ошибке
 */
@Schema(description = "Ответ с информацией об ошибке")
public record ErrorResponse(
        @Schema(description = "Сообщение об ошибке", example = "Пользователь не найден")
        String error
) {
}