package io.github.winroot33.emailnotificationservice.config;

import io.github.winroot33.emailnotificationservice.service.EmailNotificationService;
import io.github.winroot33.emailnotificationservice.service.kafka.KafkaNativeNotificationConsumer;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Конфигурация Kafka Consumer c чистым apache kafka, без spring kafka
 *
 * @author Mikhail Vasiliev (winroot123@gmail.com)
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = "application.kafka-notification-topic.consumer-type", havingValue = "native")
public class KafkaNativeConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Value("${application.kafka-notification-topic.name}")
    private String topic;

    private KafkaNativeNotificationConsumer consumer;

    @Bean
    public KafkaNativeNotificationConsumer kafkaNotificationConsumer(
            EmailNotificationService emailNotificationService) {

        consumer = new KafkaNativeNotificationConsumer(
                bootstrapServers,
                groupId,
                topic,
                emailNotificationService
        );

        consumer.start();
        log.info("Kafka native consumer started");

        return consumer;
    }

    @PreDestroy
    public void destroy() {
        if (consumer != null) {
            consumer.shutDown();
            log.info("Kafka native consumer stopped");
        }
    }
}
