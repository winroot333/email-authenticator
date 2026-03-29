package io.github.winroot33.emailnotificationservice.service;

import io.github.winroot33.dtos.NotificationMessageDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class EmailNotificationService implements NotificationService {
    private final static String NOTIFICATION_MESSAGE_TEMPLATE = """

            Уведомление для %s
            Код подтверждения: %s
            Действителен до: %s""";

    @KafkaListener(
            topics = "${application.kafka-notification-topic.name}"
    )
    @Override
    public void readNotification(NotificationMessageDto dto) {
        log.info(NOTIFICATION_MESSAGE_TEMPLATE.formatted(
                dto.getEmail(),
                dto.getCode(),
                dto.getExpiresAt().toString())
        );
    }
}
