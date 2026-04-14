package io.github.winroot33.authenticationservice.exception;

/**
 * Исключение если пользователь не найден
 *
 * @author Mikhail Vasiliev (winroot123@gmail.com)
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
