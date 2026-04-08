package io.github.winroot33.authenticationservice.service.kafka;

import io.github.winroot33.dtos.NotificationMessageDto;

public interface KafkaNotificationProducer {
    void send(NotificationMessageDto dto);
}
