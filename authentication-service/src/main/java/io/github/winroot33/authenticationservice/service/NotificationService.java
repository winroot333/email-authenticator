package io.github.winroot33.authenticationservice.service;

import io.github.winroot33.authenticationservice.entity.ConfirmationCode;
import io.github.winroot33.authenticationservice.service.kafka.KafkaNotificationProducer;
import io.github.winroot33.dtos.NotificationMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Сервис для отправки уведомлений в Kafka
 *
 * @author Mikhail Vasiliev (winroot123@gmail.com)
 */
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final KafkaNotificationProducer kafkaNotificationProducer;

    /**
     * Отправка кода подтверждения в кафку
     *
     * @param confirmationCode Код подтверждения для отправки
     */
    public void sendNotificationCode(ConfirmationCode confirmationCode) {
        var email = confirmationCode.getUser().getEmail();
        var notificationMessageDto = NotificationMessageDto.builder()
                .code(confirmationCode.getCode())
                .email(email)
                .expiresAt(confirmationCode.getExpiresAt())
                .build();
        kafkaNotificationProducer.send(notificationMessageDto);
    }
}
