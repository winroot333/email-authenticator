package io.github.winroot33.emailnotificationservice.service;

import io.github.winroot33.dtos.NotificationMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaNotificationConsumer {

    private final EmailNotificationService emailNotificationService;

    @KafkaListener(
            topics = "${application.kafka-notification-topic.name}"
    )
    public void readNotification(NotificationMessageDto dto) {
        emailNotificationService.sendNotificationCode(dto.getEmail(), dto.getCode(), dto.getExpiresAt());
    }
}
