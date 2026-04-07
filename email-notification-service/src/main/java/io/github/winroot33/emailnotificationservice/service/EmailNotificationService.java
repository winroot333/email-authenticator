package io.github.winroot33.emailnotificationservice.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Log4j2
public class EmailNotificationService {
    private static final String NOTIFICATION_MESSAGE_TEMPLATE = """

            Уведомление для %s
            Код подтверждения: %s
            Действителен до: %s""";

    public void sendNotificationCode(String email, String code, LocalDateTime expiresAt) {
        log.info(NOTIFICATION_MESSAGE_TEMPLATE.formatted(
                email,
                code,
                expiresAt.toString())
        );
    }
}
