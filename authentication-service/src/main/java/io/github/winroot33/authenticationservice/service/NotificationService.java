package io.github.winroot33.authenticationservice.service;

import io.github.winroot33.authenticationservice.entity.ConfirmationCode;
import io.github.winroot33.authenticationservice.service.kafka.KafkaNotificationProducer;
import io.github.winroot33.dtos.NotificationMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final KafkaNotificationProducer kafkaNotificationProducer;

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
