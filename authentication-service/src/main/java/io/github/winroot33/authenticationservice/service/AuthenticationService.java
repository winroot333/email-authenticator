package io.github.winroot33.authenticationservice.service;

import io.github.winroot33.authenticationservice.exception.EmailNotConfirmedException;
import io.github.winroot33.authenticationservice.security.JwtService;
import io.github.winroot33.authenticationservice.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * Сервис для логина пользователя
 *
 * @author Mikhail Vasiliev (winroot123@gmail.com)
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtService jwtService;

    public String handleLogin(String email, String password) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            return jwtService.generateToken(userDetails);

        } catch (DisabledException e) {
            throw new EmailNotConfirmedException("Подтвердите почту перед входом");
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Неверный email или пароль");
        }
    }
}
