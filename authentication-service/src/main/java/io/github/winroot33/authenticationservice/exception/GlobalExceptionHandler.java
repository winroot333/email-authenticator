package io.github.winroot33.authenticationservice.exception;

import io.github.winroot33.authenticationservice.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

/**
 * Advice для централизованной обработки исключений и возвращения ошибок по кодам
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({
            IllegalArgumentException.class,
            MethodArgumentNotValidException.class,
            HandlerMethodValidationException.class,
            UserNotFoundException.class,
            UserAlreadyExistsException.class,
            WrongConfirmationCodeException.class,
            EmailAlreadyConfirmedException.class,
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception e) {
        return createErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(BadCredentialsException e) {
        return createErrorResponse(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler({
            AccessDeniedException.class,
            EmailNotConfirmedException.class,
    })
    public ResponseEntity<ErrorResponse> handleForbidden(Exception e) {
        return createErrorResponse(HttpStatus.FORBIDDEN, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception e) {
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Server error - " + e.getMessage());
    }

    private ResponseEntity<ErrorResponse> createErrorResponse(HttpStatus status, String message) {
        return ResponseEntity
                .status(status)
                .body(new ErrorResponse(message));
    }
}
