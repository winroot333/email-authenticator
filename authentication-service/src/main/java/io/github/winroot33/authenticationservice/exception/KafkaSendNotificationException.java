package io.github.winroot33.authenticationservice.exception;

/**
 * Исключение если не удалось отправить уведомление в Kafka
 *
 * @author Mikhail Vasiliev (winroot123@gmail.com)
 */
public class KafkaSendNotificationException extends RuntimeException {
    public KafkaSendNotificationException(String message) {
        super(message);
    }
}
