package io.github.winroot33.authenticationservice.config;

import io.github.winroot33.dtos.NotificationMessageDto;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс конфигурации для Kafka Producer с использованием Spring Kafka
 *
 * @author Mikhail Vasiliev (winroot123@gmail.com)
 */
@Configuration
@EnableConfigurationProperties(KafkaConfigurationProperties.class)
@RequiredArgsConstructor
@ConditionalOnProperty(name = "application.kafka-notification-topic.producer-type", havingValue = "spring")
public class KafkaSpringConfig {

    private final KafkaConfigurationProperties kafkaConfigurationProperties;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean

    public ProducerFactory<String, NotificationMessageDto> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, NotificationMessageDto> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    NewTopic createTopic() {
        return TopicBuilder.name(kafkaConfigurationProperties.topicName())
                .partitions(kafkaConfigurationProperties.partitionCount())
                .replicas(kafkaConfigurationProperties.replicaCount())
                .build();
    }
}
