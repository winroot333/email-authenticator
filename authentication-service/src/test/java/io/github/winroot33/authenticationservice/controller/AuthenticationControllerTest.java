package io.github.winroot33.authenticationservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.winroot33.authenticationservice.dto.EmailConfirmationRequest;
import io.github.winroot33.authenticationservice.dto.ResendConfirmationRequest;
import io.github.winroot33.authenticationservice.dto.SignInRequest;
import io.github.winroot33.authenticationservice.dto.SignUpRequest;
import io.github.winroot33.authenticationservice.entity.User;
import io.github.winroot33.authenticationservice.exception.*;
import io.github.winroot33.authenticationservice.security.JwtAuthenticationFilter;
import io.github.winroot33.authenticationservice.security.JwtService;
import io.github.winroot33.authenticationservice.service.AuthenticationService;
import io.github.winroot33.authenticationservice.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Тесты контроллера аутентификации")
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AuthenticationService authenticationService;

    @Test
    @AutoConfigureMockMvc(addFilters = false)
    @DisplayName("Регистрация: успешная регистрация пользователя")
    void signUp_ShouldReturnSuccessResponse() throws Exception {
        SignUpRequest request = new SignUpRequest("test@example.com", "password123");
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .build();

        when(userService.handleRegistration(request.email(), request.password())).thenReturn(user);

        mockMvc.perform(post("/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(AuthenticationController.REGISTRATION_SUCCESS_MESSAGE))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.userId").value(1L));
    }

    @Test
    @DisplayName("Регистрация: возвращает ошибку при некорректных данных")
    void signUp_ShouldReturnBadRequest_WhenInvalidData() throws Exception {
        SignUpRequest invalidRequest = new SignUpRequest("invalid-email", "123");

        mockMvc.perform(post("/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Регистрация: возвращает ошибку при существующем пользователе")
    void signUp_ShouldReturnError_WhenUserAlreadyExists() throws Exception {
        SignUpRequest request = new SignUpRequest("existing@example.com", "password123");

        when(userService.handleRegistration(request.email(), request.password()))
                .thenThrow(new UserAlreadyExistsException("Пользователь с почтой уже существует: " + request.email()));

        mockMvc.perform(post("/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Пользователь с почтой уже существует: existing@example.com"));
    }

    @Test
    @DisplayName("Подтверждение email: успешное подтверждение")
    void confirmEmail_ShouldReturnSuccessResponse() throws Exception {
        EmailConfirmationRequest request = new EmailConfirmationRequest("test@example.com", "ABC123");

        mockMvc.perform(post("/auth/confirm-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(AuthenticationController.EMAIL_CONFIRMATION_MESSAGE));
    }

    @Test
    @DisplayName("Подтверждение email: возвращает ошибку при неверном коде")
    void confirmEmail_ShouldReturnError_WhenInvalidCode() throws Exception {
        EmailConfirmationRequest request = new EmailConfirmationRequest("test@example.com", "WRONG1");

        doThrow(new WrongConfirmationCodeException("Неверный код подтверждения"))
                .when(userService).handleEmailConfirmation(request.email(), request.confirmationCode());

        mockMvc.perform(post("/auth/confirm-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Неверный код подтверждения"));
    }

    @Test
    @DisplayName("Подтверждение email: возвращает ошибку если почта уже подтверждена")
    void confirmEmail_ShouldReturnError_WhenEmailAlreadyConfirmed() throws Exception {
        EmailConfirmationRequest request = new EmailConfirmationRequest("confirmed@example.com", "ABC123");

        doThrow(new EmailAlreadyConfirmedException("Почта уже подтверждена"))
                .when(userService).handleEmailConfirmation(request.email(), request.confirmationCode());

        mockMvc.perform(post("/auth/confirm-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Почта уже подтверждена"));
    }

    @Test
    @DisplayName("Подтверждение email: возвращает ошибку при отсутствии пользователя")
    void confirmEmail_ShouldReturnError_WhenUserNotFound() throws Exception {
        EmailConfirmationRequest request = new EmailConfirmationRequest("notfound@example.com", "ABC123");

        doThrow(new UserNotFoundException("Не найден пользователь с указанной почтой: notfound@example.com"))
                .when(userService).handleEmailConfirmation(request.email(), request.confirmationCode());

        mockMvc.perform(post("/auth/confirm-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Не найден пользователь с указанной почтой: notfound@example.com"));
    }

    @Test
    @DisplayName("Повторная отправка кода: успешная отправка")
    void resendConfirmation_ShouldReturnSuccessResponse() throws Exception {
        ResendConfirmationRequest request = new ResendConfirmationRequest("test@example.com");

        mockMvc.perform(post("/auth/resend-confirmation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(AuthenticationController.EMAIL_CONFIRMATION_RESEND_MESSAGE));
    }

    @Test
    @DisplayName("Повторная отправка кода: возвращает ошибку при отсутствии пользователя")
    void resendConfirmation_ShouldReturnError_WhenUserNotFound() throws Exception {
        ResendConfirmationRequest request = new ResendConfirmationRequest("notfound@example.com");

        doThrow(new UserNotFoundException("Не найден пользователь с указанной почтой: notfound@example.com"))
                .when(userService).resendConfirmationCode(request.email());

        mockMvc.perform(post("/auth/resend-confirmation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Не найден пользователь с указанной почтой: notfound@example.com"));
    }

    @Test
    @DisplayName("Повторная отправка кода: возвращает ошибку если почта уже подтверждена")
    void resendConfirmation_ShouldReturnError_WhenEmailAlreadyConfirmed() throws Exception {
        ResendConfirmationRequest request = new ResendConfirmationRequest("confirmed@example.com");

        doThrow(new EmailAlreadyConfirmedException("Почта уже подтверждена"))
                .when(userService).resendConfirmationCode(request.email());

        mockMvc.perform(post("/auth/resend-confirmation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Почта уже подтверждена"));
    }

    @Test
    @DisplayName("Вход: успешная аутентификация")
    void signIn_ShouldReturnJwtToken() throws Exception {
        SignInRequest request = new SignInRequest("test@example.com", "password123");
        String jwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIn0.signature";

        when(authenticationService.handleLogin(request.email(), request.password())).thenReturn(jwtToken);

        mockMvc.perform(post("/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(jwtToken));
    }

    @Test
    @DisplayName("Вход: возвращает ошибку при неверных учетных данных")
    void signIn_ShouldReturnError_WhenInvalidCredentials() throws Exception {
        SignInRequest request = new SignInRequest("test@example.com", "wrongpassword");

        when(authenticationService.handleLogin(request.email(), request.password()))
                .thenThrow(new BadCredentialsException("Неверный email или пароль"));

        mockMvc.perform(post("/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    @DisplayName("Вход: возвращает ошибку если почта не подтверждена")
    void signIn_ShouldReturnError_WhenEmailNotConfirmed() throws Exception {
        SignInRequest request = new SignInRequest("test@example.com", "password123");

        when(authenticationService.handleLogin(request.email(), request.password()))
                .thenThrow(new EmailNotConfirmedException("Почта не подтверждена"));

        mockMvc.perform(post("/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Почта не подтверждена"));
    }
}