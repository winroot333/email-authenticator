package io.github.winroot33.authenticationservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Параметры конфигурации из файла конфига для Kafka брокера
 *
 * @param topicName      имя топика
 * @param partitionCount количество партиций
 * @param replicaCount   количество реплик
 * @param producerType   тип продюсера
 *                       (native для чистого apache kafka или
 *                       spring для использования Spring Kafka)
 * @author Mikhail Vasiliev (winroot123@gmail.com)
 */
@ConfigurationProperties(prefix = "application.kafka-notification-topic")
public record KafkaConfigurationProperties(
        String topicName,
        Integer partitionCount,
        Integer replicaCount,
        ProducerType producerType
) {
    public enum ProducerType {
        NATIVE,
        SPRING
    }
}