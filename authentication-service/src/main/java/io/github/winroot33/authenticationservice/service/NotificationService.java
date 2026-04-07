package io.github.winroot33.authenticationservice.service;

import io.github.winroot33.authenticationservice.config.KafkaConfigurationProperties;
import io.github.winroot33.authenticationservice.entity.ConfirmationCode;
import io.github.winroot33.authenticationservice.exception.KafkaSendNotificationException;
import io.github.winroot33.dtos.NotificationMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final KafkaTemplate<String, NotificationMessageDto> kafkaTemplate;

    private final KafkaConfigurationProperties kafkaConfig;

    public void sendNotificationCode(ConfirmationCode confirmationCode) {
        var email = confirmationCode.getUser().getEmail();
        var notificationMessageDto = NotificationMessageDto.builder()
                .code(confirmationCode.getCode())
                .email(email)
                .expiresAt(confirmationCode.getExpiresAt())
                .build();
        try {
            var result = kafkaTemplate.send(kafkaConfig.topicName(), email, notificationMessageDto).get();
            log.info("Notification sent successfully: offset={}, partition={}",
                    result.getRecordMetadata().offset(),
                    result.getRecordMetadata().partition());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new KafkaSendNotificationException("Не удалось отправить код подтверждения, попробуйте заново");
        } catch (ExecutionException e) {
            throw new KafkaSendNotificationException("Не удалось отправить код подтверждения, попробуйте заново");
        }
    }
}
