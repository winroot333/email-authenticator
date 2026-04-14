package io.github.winroot33.authenticationservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Конфигурация cors для Spring Security
 *
 * @author Mikhail Vasiliev (winroot123@gmail.com)
 */
@Configuration
@EnableConfigurationProperties(CorsConfigurationProperties.class)
@RequiredArgsConstructor
public class CorsConfig {

    private final CorsConfigurationProperties corsProperties;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(corsProperties.allowedOrigins());
        configuration.setAllowedMethods(Arrays.asList(corsProperties.allowedMethods().split(",")));
        configuration.setAllowedHeaders(Arrays.asList(corsProperties.allowedHeaders().split(",")));
        configuration.setAllowCredentials(corsProperties.allowCredentials());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}