package io.github.winroot33.emailnotificationservice.config;

import io.github.winroot33.dtos.NotificationMessageDto;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    public static final String TRUSTED_DTO_PACKAGE = "io.github.winroot33.dtos";
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Bean
    public ConsumerFactory<String, NotificationMessageDto> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapAddress);
        props.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                groupId);
        props.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class);
        props.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                ErrorHandlingDeserializer.class);
        props.put(
                ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS,
                JacksonJsonDeserializer.class);
        props.put(
                JacksonJsonDeserializer.TRUSTED_PACKAGES,
                TRUSTED_DTO_PACKAGE);
        props.put(
                JacksonJsonDeserializer.VALUE_DEFAULT_TYPE,
                NotificationMessageDto.class);

        //todo эти проперти добавить
//        #      auto-offset-reset: earliest
//#      enable-auto-commit: false

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, NotificationMessageDto>
    kafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, NotificationMessageDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
