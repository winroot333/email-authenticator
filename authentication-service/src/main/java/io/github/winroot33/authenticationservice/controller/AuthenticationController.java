package io.github.winroot33.authenticationservice.controller;

import io.github.winroot33.authenticationservice.dto.JwtAuthenticationResponse;
import io.github.winroot33.authenticationservice.dto.SignUpRequest;
import io.github.winroot33.authenticationservice.service.UserService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Аутентификация")
@ApiResponses(@ApiResponse(responseCode = "200", useReturnTypeSchema = true))
public class AuthenticationController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/sign-up")
    public JwtAuthenticationResponse signUp(@RequestBody @Valid SignUpRequest request) {
        String token = userService.handleRegistration(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword())
        );
        return new JwtAuthenticationResponse(token);
    }
}
