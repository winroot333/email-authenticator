package io.github.winroot33.authenticationservice.service.kafka;

import io.github.winroot33.dtos.NotificationMessageDto;

/**
 * Интерфейс для отправки кодов подтверждения в kafka
 *
 * @author Mikhail Vasiliev (winroot123@gmail.com)
 */
public interface KafkaNotificationProducer {
    /**
     * Отправка кода подтверждения в kafka
     *
     * @param dto DTO для отправки
     */
    void send(NotificationMessageDto dto);
}
