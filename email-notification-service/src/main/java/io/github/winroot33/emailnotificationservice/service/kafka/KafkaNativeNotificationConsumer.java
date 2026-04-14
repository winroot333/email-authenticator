package io.github.winroot33.emailnotificationservice.service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.winroot33.dtos.NotificationMessageDto;
import io.github.winroot33.emailnotificationservice.service.EmailNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Consumer для отправки кодов подтверждения без Spring Kafka
 *
 * @author Mikhail Vasiliev (winroot123@gmail.com)
 */
@Slf4j
public class KafkaNativeNotificationConsumer implements AutoCloseable {

    private final KafkaConsumer<String, String> consumer;
    private final EmailNotificationService emailNotificationService;
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final String topic;
    private final ObjectMapper objectMapper;

    public KafkaNativeNotificationConsumer(String bootstrapServers,
                                           String groupId,
                                           String topic,
                                           EmailNotificationService emailNotificationService) {
        this.topic = topic;
        this.emailNotificationService = emailNotificationService;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        this.consumer = new KafkaConsumer<>(props);
    }

    public void start() {
        consumer.subscribe(List.of(topic));

        Thread consumerThread = new Thread(this::pollLoop);
        consumerThread.setName("kafka-consumer-thread");
        consumerThread.start();
    }

    private void pollLoop() {
        try {
            while (running.get()) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, String> record : records) {
                    processMessage(record);
                }
            }
        } catch (WakeupException e) {
            log.info("Consumer wakeup called, shutting down");
        } catch (Exception e) {
            log.error("Unexpected error in consumer loop", e);
        } finally {
            try {
                consumer.commitSync();
            } finally {
                consumer.close();
            }
        }
    }

    private void processMessage(ConsumerRecord<String, String> record) {
        try {
            NotificationMessageDto dto = objectMapper.readValue(
                    record.value(),
                    NotificationMessageDto.class
            );

            emailNotificationService.sendNotificationCode(
                    dto.getEmail(), dto.getCode(), dto.getExpiresAt());

            consumer.commitSync();

            log.info("Native Kafka Successfully processed message: offset = {}, partition = {}",
                    record.offset(), record.partition());
        } catch (Exception e) {
            log.error("Native Kafka Failed to process message: offset = {}, partition = {}",
                    record.offset(), record.partition(), e);
        }
    }

    public void shutDown() {
        running.set(false);
        consumer.wakeup();
    }

    @Override
    public void close() {
        shutDown();
    }
}