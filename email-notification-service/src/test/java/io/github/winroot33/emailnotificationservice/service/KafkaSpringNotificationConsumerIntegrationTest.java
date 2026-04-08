package io.github.winroot33.emailnotificationservice.service;

import io.github.winroot33.dtos.NotificationMessageDto;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.*;

@SpringBootTest
@EmbeddedKafka
class KafkaSpringNotificationConsumerIntegrationTest {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;
    @Autowired
    private Environment env;
    @MockitoBean
    private EmailNotificationService emailNotificationService;

    @Test
    @DisplayName("Сервис успешно получает и обрабатывает сообщение из Kafka")
    void readNotification() throws ExecutionException, InterruptedException {
        LocalDateTime expiresAt = LocalDateTime.of(2026, 4, 12, 12, 10, 15);
        NotificationMessageDto dto = NotificationMessageDto.builder()
                .code("123456")
                .email("test@example.com")
                .expiresAt(expiresAt)
                .build();

        sendKafkaMessage("test@example.com", dto);

        verify(emailNotificationService, timeout(5000).times(1))
                .sendNotificationCode("test@example.com", "123456", expiresAt);

    }

    private void sendKafkaMessage(String key, NotificationMessageDto dto) throws ExecutionException, InterruptedException {
        Map<String, Object> producerProps = new HashMap<>();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafkaBroker.getBrokersAsString());
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        DefaultKafkaProducerFactory<String, NotificationMessageDto> pf =
                new DefaultKafkaProducerFactory<>(producerProps);
        var kafkaTemplate = new KafkaTemplate<>(pf);
        String topic = env.getProperty("application.kafka-notification-topic.name");
        kafkaTemplate.send(topic, key, dto).get();

        pf.destroy();
    }


    @Test
    @DisplayName("Сервис корректно обрабатывает невалидное сообщение")
    void testConsumerHandlesInvalidMessage() throws Exception {
        var invalidData = "{\"wrongField\":\"value\", \"code\":123, \"email\":\"test@example.com\"}";
        sendInvalidKafkaMessage("test@example.com", invalidData);

        Thread.sleep(2000);
        verify(emailNotificationService, never())
                .sendNotificationCode(any(), any(), any());
    }

    private void sendInvalidKafkaMessage(String key, String invalidData) throws Exception {
        Map<String, Object> producerProps = new HashMap<>();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafkaBroker.getBrokersAsString());
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        DefaultKafkaProducerFactory<String, String> pf = new DefaultKafkaProducerFactory<>(producerProps);
        var kafkaTemplate = new KafkaTemplate<>(pf);
        String topic = env.getProperty("application.kafka-notification-topic.name");
        kafkaTemplate.send(topic, key, invalidData).get();

        pf.destroy();
    }
}