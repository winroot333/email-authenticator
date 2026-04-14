package io.github.winroot33.authenticationservice.exception;

/**
 * Исключение если почта уже подтверждена
 *
 * @author Mikhail Vasiliev (winroot123@gmail.com)
 */
public class EmailAlreadyConfirmedException extends RuntimeException {
    public EmailAlreadyConfirmedException(String message) {
        super(message);
    }
}
