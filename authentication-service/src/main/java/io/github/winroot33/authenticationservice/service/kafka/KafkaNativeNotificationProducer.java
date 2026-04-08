package io.github.winroot33.authenticationservice.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.winroot33.authenticationservice.config.KafkaConfigurationProperties;
import io.github.winroot33.authenticationservice.exception.KafkaSendNotificationException;
import io.github.winroot33.dtos.NotificationMessageDto;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.concurrent.ExecutionException;


@Component
@ConditionalOnProperty(name = "application.kafka-notification-topic.producer-type", havingValue = "native")
@Slf4j
@RequiredArgsConstructor
public class KafkaNativeNotificationProducer implements KafkaNotificationProducer {
    private final KafkaConfigurationProperties kafkaConfig;
    private KafkaProducer<String, String> producer;
    private ObjectMapper objectMapper;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @PostConstruct
    public void init() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);

        producer = new KafkaProducer<>(props);

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @PreDestroy
    public void destroy() {
        if (producer != null) {
            producer.flush();
            producer.close();
        }
    }

    @Override
    public void send(NotificationMessageDto dto) {
        try {
            String json = objectMapper.writeValueAsString(dto);
            ProducerRecord<String, String> producerRecord = new ProducerRecord<>(
                    kafkaConfig.topicName(),
                    dto.getEmail(),
                    json
            );
            producerRecord.headers().add(
                    new RecordHeader(
                            "__TypeId__",
                            NotificationMessageDto.class.getName().getBytes(StandardCharsets.UTF_8)
                    ));

            RecordMetadata metadata = producer.send(producerRecord).get();
            log.info("Native Kafka sent: offset={}, partition={}",
                    metadata.offset(), metadata.partition());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new KafkaSendNotificationException("Не удалось отправить сообщение");
        } catch (ExecutionException e) {
            throw new KafkaSendNotificationException("Не удалось отправить сообщение");
        } catch (JsonProcessingException e) {
            throw new KafkaSendNotificationException("Ошибка сериализации, не удалось отправить сообщение");
        }
    }
}
