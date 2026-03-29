package io.github.winroot33.emailnotificationservice.service;

import io.github.winroot33.dtos.NotificationMessageDto;


public interface NotificationService {
    public void readNotification(NotificationMessageDto notificationMessageDto);
}
