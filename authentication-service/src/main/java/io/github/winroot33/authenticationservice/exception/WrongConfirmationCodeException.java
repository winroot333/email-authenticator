package io.github.winroot33.authenticationservice.exception;

public class WrongConfirmationCodeException extends RuntimeException {
    public WrongConfirmationCodeException(String message) {
        super(message);
    }
}
