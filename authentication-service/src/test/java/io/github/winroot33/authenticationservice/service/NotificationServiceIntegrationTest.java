package io.github.winroot33.authenticationservice.service;

import io.github.winroot33.authenticationservice.entity.ConfirmationCode;
import io.github.winroot33.authenticationservice.entity.User;
import io.github.winroot33.authenticationservice.service.kafka.KafkaNativeTopicManager;
import io.github.winroot33.dtos.NotificationMessageDto;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
@ActiveProfiles("test")
@AutoConfigureTestDatabase
@EmbeddedKafka
@SpringBootTest()

@DisplayName("Интеграционные тесты с Kafka")
class NotificationServiceIntegrationTest {

    @Autowired
    Environment env;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @MockitoBean
    private KafkaNativeTopicManager kafkaNativeTopicManager;

    private KafkaMessageListenerContainer<String, NotificationMessageDto> container;
    private BlockingQueue<ConsumerRecord<String, NotificationMessageDto>> records;

    @BeforeAll
    void setUp() {
        var consumerFactory = new DefaultKafkaConsumerFactory<>(getConsumerProperties());
        ContainerProperties containerProperties = new ContainerProperties(
                env.getProperty("application.kafka-notification-topic.topic-name"));

        container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
        records = new LinkedBlockingQueue<>();
        container.setupMessageListener((MessageListener<String, NotificationMessageDto>) records::add);
        container.start();
        ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());
    }

    @AfterAll
    void tearDown() {
        container.stop();
    }

    private Map<String, Object> getConsumerProperties() {
        var configProps = new HashMap<String, Object>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafkaBroker.getBrokersAsString());
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        configProps.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, env.getProperty("spring.kafka.consumer.group-id"));
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, env.getProperty("spring.kafka.consumer.auto-offset-reset"));
        configProps.put(
                JsonDeserializer.TRUSTED_PACKAGES,
                env.getProperty("spring.kafka.consumer.properties.spring.json.trusted.packages")
        );
        return configProps;
    }

    @Test
    @DisplayName("Отправка сообщения в кафку: успешно отправляет корректное сообщение")
    void sendNotification() throws InterruptedException {
        LocalDateTime expiresAt = LocalDateTime.of(2026, 4, 12, 12, 10, 15);
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .build();
        ConfirmationCode confirmationCode = ConfirmationCode.builder()
                .code("123456")
                .user(user)
                .expiresAt(expiresAt)
                .build();
        NotificationMessageDto dto = NotificationMessageDto.builder()
                .code("123456")
                .email("test@example.com")
                .expiresAt(expiresAt)
                .build();

        notificationService.sendNotificationCode(confirmationCode);

        ConsumerRecord<String, NotificationMessageDto> message = records.poll(3000, TimeUnit.MILLISECONDS);
        assertThat(message).isNotNull();
        assertThat(message.key()).isEqualTo("test@example.com");
        assertThat(message.value()).isEqualTo(dto);
    }
}
