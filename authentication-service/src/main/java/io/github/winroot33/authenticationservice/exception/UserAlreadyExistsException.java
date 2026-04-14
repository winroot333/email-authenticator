package io.github.winroot33.authenticationservice.exception;

/**
 * Исключение если пользователь с необходимыми данными уже существует
 *
 * @author Mikhail Vasiliev (winroot123@gmail.com)
 */
public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
