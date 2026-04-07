package io.github.winroot33.authenticationservice.service;

import io.github.winroot33.authenticationservice.entity.ConfirmationCode;
import io.github.winroot33.authenticationservice.entity.User;
import io.github.winroot33.authenticationservice.exception.EmailAlreadyConfirmedException;
import io.github.winroot33.authenticationservice.exception.UserAlreadyExistsException;
import io.github.winroot33.authenticationservice.exception.UserNotFoundException;
import io.github.winroot33.authenticationservice.exception.WrongConfirmationCodeException;
import io.github.winroot33.authenticationservice.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты сервиса пользователей")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ConfirmationCodeService confirmationCodeService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Регистрация: успешная регистрация нового пользователя")
    void handleRegistration_ShouldRegisterNewUser() {
        String email = "test@example.com";
        String password = "password123";
        String encodedPassword = "encodedPassword";
        User savedUser = User.builder()
                .id(1L)
                .email(email)
                .password(encodedPassword)
                .emailConfirmed(false)
                .build();
        ConfirmationCode confirmationCode = ConfirmationCode.builder()
                .code("ABC123")
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .user(savedUser)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(confirmationCodeService.createForUser(savedUser)).thenReturn(confirmationCode);

        User result = userService.handleRegistration(email, password);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.isEmailConfirmed()).isFalse();

        verify(userRepository).findByEmail(email);
        verify(passwordEncoder).encode(password);
        verify(userRepository).save(any(User.class));
        verify(confirmationCodeService).createForUser(savedUser);
        verify(notificationService).sendNotificationCode(confirmationCode);
    }

    @Test
    @DisplayName("Регистрация: выбрасывает исключение при существующем email")
    void handleRegistration_ShouldThrowException_WhenEmailExists() {
        String email = "existing@example.com";
        String password = "password123";
        User existingUser = User.builder().email(email).build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> userService.handleRegistration(email, password))
                .isInstanceOf(UserAlreadyExistsException.class);

        verify(userRepository).findByEmail(email);
        verify(userRepository, never()).save(any(User.class));
        verify(confirmationCodeService, never()).createForUser(any());
        verify(notificationService, never()).sendNotificationCode(any());
    }

    @Test
    @DisplayName("Повторная отправка кода: успешная отправка для существующего пользователя")
    void resendConfirmationCode_ShouldSendCode() {
        String email = "test@example.com";
        User user = User.builder()
                .id(1L)
                .email(email)
                .emailConfirmed(false)
                .build();
        ConfirmationCode confirmationCode = ConfirmationCode.builder()
                .code("XYZ789")
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .user(user)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(confirmationCodeService.createForUser(user)).thenReturn(confirmationCode);

        userService.resendConfirmationCode(email);

        verify(userRepository).findByEmail(email);
        verify(confirmationCodeService).createForUser(user);
        verify(notificationService).sendNotificationCode(confirmationCode);
    }

    @Test
    @DisplayName("Повторная отправка кода: выбрасывает исключение при отсутствии пользователя")
    void resendConfirmationCode_ShouldThrowException_WhenUserNotFound() {
        String email = "notfound@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.resendConfirmationCode(email))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository).findByEmail(email);
        verify(confirmationCodeService, never()).createForUser(any());
        verify(notificationService, never()).sendNotificationCode(any());
    }

    @Test
    @DisplayName("Повторная отправка кода: выбрасывает исключение если почта уже подтверждена")
    void resendConfirmationCode_ShouldThrowException_WhenEmailAlreadyConfirmed() {
        String email = "confirmed@example.com";
        User user = User.builder()
                .id(1L)
                .email(email)
                .emailConfirmed(true)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.resendConfirmationCode(email))
                .isInstanceOf(EmailAlreadyConfirmedException.class);

        verify(userRepository).findByEmail(email);
        verify(confirmationCodeService, never()).createForUser(any());
        verify(notificationService, never()).sendNotificationCode(any());
    }

    @Test
    @DisplayName("Подтверждение email: успешное подтверждение")
    void handleEmailConfirmation_ShouldConfirmEmail() {
        String email = "test@example.com";
        String confirmationCode = "ABC123";
        User user = User.builder()
                .id(1L)
                .email(email)
                .emailConfirmed(false)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        userService.handleEmailConfirmation(email, confirmationCode);

        verify(userRepository).findByEmail(email);
        verify(confirmationCodeService).validateCode(user.getId(), confirmationCode);
        verify(userRepository).save(user);
        assertThat(user.isEmailConfirmed()).isTrue();
    }

    @Test
    @DisplayName("Подтверждение email: выбрасывает исключение при отсутствии пользователя")
    void handleEmailConfirmation_ShouldThrowException_WhenUserNotFound() {
        String email = "notfound@example.com";
        String confirmationCode = "ABC123";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.handleEmailConfirmation(email, confirmationCode))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository).findByEmail(email);
        verify(confirmationCodeService, never()).validateCode(any(), any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Подтверждение email: выбрасывает исключение если почта уже подтверждена")
    void handleEmailConfirmation_ShouldThrowException_WhenEmailAlreadyConfirmed() {
        String email = "confirmed@example.com";
        String confirmationCode = "ABC123";
        User user = User.builder()
                .id(1L)
                .email(email)
                .emailConfirmed(true)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.handleEmailConfirmation(email, confirmationCode))
                .isInstanceOf(EmailAlreadyConfirmedException.class);

        verify(userRepository).findByEmail(email);
        verify(confirmationCodeService, never()).validateCode(any(), any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Подтверждение email: выбрасывает исключение при неверном коде")
    void handleEmailConfirmation_ShouldThrowException_WhenCodeInvalid() {
        String email = "test@example.com";
        String invalidCode = "WRONG";
        User user = User.builder()
                .id(1L)
                .email(email)
                .emailConfirmed(false)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        doThrow(new WrongConfirmationCodeException("Неверный код подтверждения"))
                .when(confirmationCodeService).validateCode(user.getId(), invalidCode);

        assertThatThrownBy(() -> userService.handleEmailConfirmation(email, invalidCode))
                .isInstanceOf(WrongConfirmationCodeException.class);

        verify(userRepository).findByEmail(email);
        verify(confirmationCodeService).validateCode(user.getId(), invalidCode);
        verify(userRepository, never()).save(any());
    }
}