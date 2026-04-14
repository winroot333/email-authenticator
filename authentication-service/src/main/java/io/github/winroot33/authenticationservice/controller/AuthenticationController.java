package io.github.winroot33.authenticationservice.controller;

import io.github.winroot33.authenticationservice.dto.*;
import io.github.winroot33.authenticationservice.entity.User;
import io.github.winroot33.authenticationservice.service.AuthenticationService;
import io.github.winroot33.authenticationservice.service.UserService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для регистрации, логина, подтверждения почты
 *
 * @author Mikhail Vasiliev (winroot123@gmail.com)
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Аутентификация")
@ApiResponses(@ApiResponse(responseCode = "200", useReturnTypeSchema = true))
public class AuthenticationController {
    public static final String REGISTRATION_SUCCESS_MESSAGE = "Пользователь создан, подтвердите вашу почту";
    public static final String EMAIL_CONFIRMATION_MESSAGE = "Почта успешно подтверждена";
    public static final String EMAIL_CONFIRMATION_RESEND_MESSAGE = "Код подтверждения отправлен";

    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/sign-up")
    public SignUpResponse signUp(@RequestBody @Valid SignUpRequest request) {
        User user = userService.handleRegistration(request.email(), request.password());
        return new SignUpResponse(REGISTRATION_SUCCESS_MESSAGE, user.getEmail(), user.getId());
    }

    @PostMapping("/confirm-email")
    public EmailConfirmationResponse confirmEmail(@RequestBody @Valid EmailConfirmationRequest request) {
        userService.handleEmailConfirmation(
                request.email(),
                request.confirmationCode()
        );
        return new EmailConfirmationResponse(EMAIL_CONFIRMATION_MESSAGE);
    }

    @PostMapping("/resend-confirmation")
    public EmailConfirmationResponse resendConfirmation(@RequestBody @Valid ResendConfirmationRequest request) {
        userService.resendConfirmationCode(request.email());
        return new EmailConfirmationResponse(EMAIL_CONFIRMATION_RESEND_MESSAGE);
    }

    @PostMapping("/sign-in")
    public SignInResponse singIn(@RequestBody @Valid SignInRequest request) {
        var jwt = authenticationService.handleLogin(request.email(), request.password());
        return new SignInResponse(jwt);
    }
}
