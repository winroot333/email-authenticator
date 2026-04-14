package io.github.winroot33.authenticationservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO для ответа после успешной регистрации
 *
 * @author Mikhail Vasiliev (winroot123@gmail.com)
 */
@Schema(description = "Ответ после регистрации с id пользователя")
public record SignUpResponse(
        @Schema(description = "Сообщение для пользователя", example = "Пользователь создан, подтвердите вашу почту")
        String message,

        @Schema(description = "Email для подтверждения")
        String email,

        @Schema(description = "Id созданного пользователя", example = "2")
        Long userId
) {
}
