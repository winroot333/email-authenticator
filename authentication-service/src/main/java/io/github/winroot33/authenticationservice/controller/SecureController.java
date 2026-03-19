package io.github.winroot33.authenticationservice.controller;

import io.github.winroot33.authenticationservice.dto.EmailConfirmationResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/secure")
@Tag(name = "Защищенный эндпоинт")
@ApiResponses(@ApiResponse(responseCode = "200", useReturnTypeSchema = true))
public class SecureController {

    @GetMapping
    public EmailConfirmationResponse secureEndpoint(Authentication authentication) {
        return new EmailConfirmationResponse("Welcome " + authentication.getName());
    }
}
