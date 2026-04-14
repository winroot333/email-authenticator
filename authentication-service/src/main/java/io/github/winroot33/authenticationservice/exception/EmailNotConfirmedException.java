package io.github.winroot33.authenticationservice.exception;

/**
 * Исключение если пользователь не подтвержден
 *
 * @author Mikhail Vasiliev (winroot123@gmail.com)
 */
public class EmailNotConfirmedException extends RuntimeException {
    public EmailNotConfirmedException(String message) {
        super(message);
    }
}
