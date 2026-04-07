package io.github.winroot33.authenticationservice.service;

import io.github.winroot33.authenticationservice.entity.ConfirmationCode;
import io.github.winroot33.authenticationservice.entity.User;
import io.github.winroot33.authenticationservice.exception.WrongConfirmationCodeException;
import io.github.winroot33.authenticationservice.repository.ConfirmationCodeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты сервиса кодов подтверждения")
class ConfirmationCodeServiceTest {

    @Mock
    private ConfirmationCodeRepository confirmationCodeRepository;

    @InjectMocks
    private ConfirmationCodeService confirmationCodeService;

    @Test
    @DisplayName("Создание кода подтверждения: успешное создание с корректными данными")
    void createForUser_ShouldCreateAndReturnCode() {
        User user = User.builder().id(1L).build();
        ReflectionTestUtils.setField(confirmationCodeService, "confirmationCodeExpirationMinutes", 30);

        when(confirmationCodeRepository.save(any(ConfirmationCode.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ConfirmationCode result = confirmationCodeService.createForUser(user);

        assertThat(result).isNotNull();
        assertThat(result.getUser()).isEqualTo(user);
        assertThat(result.getCode()).isNotNull().hasSize(6);
        assertThat(result.getCode()).isUpperCase();
        assertThat(result.getExpiresAt())
                .isAfter(LocalDateTime.now());

        verify(confirmationCodeRepository).save(any(ConfirmationCode.class));
    }

    @Test
    @DisplayName("Валидация кода: успешная валидация при корректном коде")
    void validateCode_ShouldSucceed_WhenCodeIsValid() {
        Long userId = 1L;
        String code = "ABC123";
        when(confirmationCodeRepository
                .existsByUserIdAndCodeAndExpiresAtAfter(eq(userId), eq(code), any(LocalDateTime.class)))
                .thenReturn(true);


        confirmationCodeService.validateCode(userId, code);

        verify(confirmationCodeRepository)
                .existsByUserIdAndCodeAndExpiresAtAfter(eq(userId), eq(code), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Валидация кода: выбрасывает исключение при неверном коде")
    void validateCode_ShouldThrowException_WhenCodeIsInvalid() {
        Long userId = 1L;
        String invalidCode = "WRONG";
        when(confirmationCodeRepository
                .existsByUserIdAndCodeAndExpiresAtAfter(eq(userId), eq(invalidCode), any(LocalDateTime.class)))
                .thenReturn(false);

        assertThatThrownBy(() -> confirmationCodeService.validateCode(userId, invalidCode))
                .isInstanceOf(WrongConfirmationCodeException.class);

        verify(confirmationCodeRepository)
                .existsByUserIdAndCodeAndExpiresAtAfter(eq(userId), eq(invalidCode), any(LocalDateTime.class));
    }
}