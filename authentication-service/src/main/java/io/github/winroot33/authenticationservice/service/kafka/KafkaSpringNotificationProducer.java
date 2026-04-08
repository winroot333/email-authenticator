package io.github.winroot33.authenticationservice.service.kafka;

import io.github.winroot33.authenticationservice.config.KafkaConfigurationProperties;
import io.github.winroot33.authenticationservice.exception.KafkaSendNotificationException;
import io.github.winroot33.dtos.NotificationMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@ConditionalOnProperty(name = "application.kafka-notification-topic.producer-type", havingValue = "spring")
@RequiredArgsConstructor
@Slf4j
@Component
public class KafkaSpringNotificationProducer implements KafkaNotificationProducer {

    private final KafkaTemplate<String, NotificationMessageDto> kafkaTemplate;

    private final KafkaConfigurationProperties kafkaConfig;

    @Override
    public void send(NotificationMessageDto dto) {

        try {
            var result = kafkaTemplate.send(kafkaConfig.topicName(), dto.getEmail(), dto).get();
            log.info("Spring Kafka sent successfully: offset={}, partition={}",
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
