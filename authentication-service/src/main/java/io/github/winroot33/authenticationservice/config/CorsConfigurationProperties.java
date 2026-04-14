package io.github.winroot33.authenticationservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Свойства конфигурации для cors из файла конфига
 *
 * @author Mikhail Vasiliev (winroot123@gmail.com)
 */
@ConfigurationProperties(prefix = "cors")
public record CorsConfigurationProperties(
        List<String> allowedOrigins,
        String allowedMethods,
        String allowedHeaders,
        Boolean allowCredentials
) {
}
