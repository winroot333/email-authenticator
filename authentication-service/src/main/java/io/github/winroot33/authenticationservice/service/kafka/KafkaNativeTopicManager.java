package io.github.winroot33.authenticationservice.service.kafka;

import io.github.winroot33.authenticationservice.config.KafkaConfigurationProperties;
import io.github.winroot33.authenticationservice.exception.KafkaSendNotificationException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * Класс для создания топика при старте приложения с нативным apache kafka
 *
 * @author Mikhail Vasiliev (winroot123@gmail.com)
 */
@Component
@ConditionalOnProperty(name = "application.kafka-notification-topic.producer-type", havingValue = "native")
@RequiredArgsConstructor
@Slf4j
public class KafkaNativeTopicManager {

    private final KafkaConfigurationProperties kafkaConfig;

    private AdminClient adminClient;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @PostConstruct
    public void init() {
        Properties adminProps = new Properties();
        adminProps.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        adminClient = AdminClient.create(adminProps);

        createTopicIfNotExists();
        adminClient.close();
    }

    public void createTopicIfNotExists() {
        try {
            if (!topicExists(kafkaConfig.topicName())) {
                createTopic(
                        kafkaConfig.topicName(),
                        kafkaConfig.partitionCount(),
                        kafkaConfig.replicaCount().shortValue()
                );
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new KafkaSendNotificationException("Не удалось создать топик: " + kafkaConfig.topicName());
        } catch (Exception e) {
            throw new KafkaSendNotificationException("Не удалось создать топик: " + kafkaConfig.topicName());
        }
    }

    private void createTopic(String topicName, int partitions, short replicas)
            throws ExecutionException, InterruptedException {
        NewTopic newTopic = new NewTopic(topicName, partitions, replicas);
        adminClient.createTopics(Collections.singleton(newTopic)).all().get();
        log.info("Topic '{}' created with {} partitions and {} replicas",
                topicName, partitions, replicas);
    }

    private boolean topicExists(String topicName) throws ExecutionException, InterruptedException {
        Set<String> topics = adminClient.listTopics().names().get();
        return topics.contains(topicName);
    }
}
