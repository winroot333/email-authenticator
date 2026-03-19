package io.github.winroot33.authenticationservice.service;

import io.github.winroot33.authenticationservice.entity.ConfirmationCode;
import io.github.winroot33.authenticationservice.entity.User;
import io.github.winroot33.authenticationservice.exception.WrongConfirmationCodeException;
import io.github.winroot33.authenticationservice.repository.ConfirmationCodeRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ConfirmationCodeService {
    private final ConfirmationCodeRepository confirmationCodeRepository;
    @Value("${application.confirmation-code.expiration-in-minutes}")
    private Integer confirmationCodeExpirationMinutes;

    public ConfirmationCode generateForUser(User user) {
        var code = ConfirmationCode.builder()
                .code(generateCode())
                .expiresAt(LocalDateTime.now().plusMinutes(confirmationCodeExpirationMinutes))
                .user(user)
                .build();
        return confirmationCodeRepository.save(code);
    }

    public void validateCode(Long userId, String code) {
        boolean isValid = confirmationCodeRepository
                .existsByUserIdAndCodeAndExpiresAtAfter(userId, code, LocalDateTime.now());

        if (!isValid) {
            throw new WrongConfirmationCodeException("Неверный код подтверждения");
        }
    }

    private String generateCode() {
        return RandomStringUtils.secure().nextAlphanumeric(6).toUpperCase();
    }
}
