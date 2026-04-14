package io.github.winroot33.authenticationservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO для ответа после успешного логина
 *
 * @author Mikhail Vasiliev (winroot123@gmail.com)
 */
@Schema(description = "Ответ c токеном доступа")
public record SignInResponse(
        @Schema(description = "Токен доступа", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCYyMjUwNj...")
        String token
) {
}
