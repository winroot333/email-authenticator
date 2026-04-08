package io.github.winroot33.authenticationservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

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