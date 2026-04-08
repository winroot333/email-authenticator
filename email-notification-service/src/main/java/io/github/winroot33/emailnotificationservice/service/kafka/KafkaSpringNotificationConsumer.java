package io.github.winroot33.emailnotificationservice.service.kafka;

import io.github.winroot33.dtos.NotificationMessageDto;
import io.github.winroot33.emailnotificationservice.service.EmailNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "application.kafka-notification-topic.consumer-type", havingValue = "spring")
public class KafkaSpringNotificationConsumer {

    private final EmailNotificationService emailNotificationService;

    @KafkaListener(
            topics = "${application.kafka-notification-topic.name}"
    )
    public void readNotification(NotificationMessageDto dto, Acknowledgment ack) {
        emailNotificationService.sendNotificationCode(dto.getEmail(), dto.getCode(), dto.getExpiresAt());
        log.info("Spring Consumer Successfully processed message");
        ack.acknowledge();
    }
}
