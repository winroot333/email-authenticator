package io.github.winroot33.authenticationservice.service;

import io.github.winroot33.authenticationservice.entity.User;
import io.github.winroot33.authenticationservice.exception.EmailAlreadyConfirmedException;
import io.github.winroot33.authenticationservice.exception.UserAlreadyExistsException;
import io.github.winroot33.authenticationservice.exception.UserNotFoundException;
import io.github.winroot33.authenticationservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final ConfirmationCodeService confirmationCodeService;
    private final PasswordEncoder passwordEncoder;

    private static void checkEmailNotConfirmed(User user) {
        if (user.isEmailConfirmed()) {
            throw new EmailAlreadyConfirmedException("Почта уже подтверждена");
        }
    }

    @Transactional
    public User handleRegistration(String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException("Пользователь с почтой уже существует: " + email);
        }
        var newUser = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .emailConfirmed(false)
                .build();
        var savedUser = userRepository.save(newUser);

        sendConfirmationCode(savedUser);

        return savedUser;
    }

    @Transactional
    public void resendConfirmationCode(String email) {
        var user = findUserWithCheck(email);
        sendConfirmationCode(user);
    }

    @Transactional
    public void handleEmailConfirmation(String email, String confirmationCode) {
        var user = findUserWithCheck(email);

        checkEmailNotConfirmed(user);
        confirmationCodeService.validateCode(user.getId(), confirmationCode);

        user.setEmailConfirmed(true);
        userRepository.save(user);
    }

    private User findUserWithCheck(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Не найден пользователь с указанной почтой: " + email));
    }

    private void sendConfirmationCode(User user) {
        checkEmailNotConfirmed(user);
        var confirmationCode = confirmationCodeService.generateForUser(user);
        //todo закомментировать потом
        System.out.println(confirmationCode.getCode());
    }
}
