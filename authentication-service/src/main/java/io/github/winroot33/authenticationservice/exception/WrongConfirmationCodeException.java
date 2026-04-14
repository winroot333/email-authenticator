package io.github.winroot33.authenticationservice.exception;

/**
 * Исключение при неверном коде подтверждения почты
 *
 * @author Mikhail Vasiliev (winroot123@gmail.com)
 */
public class WrongConfirmationCodeException extends RuntimeException {
    public WrongConfirmationCodeException(String message) {
        super(message);
    }
}
