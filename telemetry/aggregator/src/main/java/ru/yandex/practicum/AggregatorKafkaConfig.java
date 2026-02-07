package ru.yandex.practicum;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class AggregatorKafkaConfig {
    private final Environment environment;

    @Bean
    public KafkaConsumer<String, SpecificRecordBase> getConsumer() {
        Properties config = new Properties();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, environment.getProperty("spring.kafka.bootstrap-servers"));
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                environment.getProperty("spring.kafka.consumer.key-deserializer"));
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                environment.getProperty("spring.kafka.consumer.value-deserializer"));
        config.put(ConsumerConfig.GROUP_ID_CONFIG, environment.getProperty("spring.kafka.consumer.group-id"));
        config.put(ConsumerConfig.CLIENT_ID_CONFIG, environment.getProperty("spring.kafka.consumer.client-id"));
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
                environment.getProperty("spring.kafka.consumer.enable-auto-commit"));

        return new KafkaConsumer<>(config);
    }

    @Bean
    public KafkaProducer<String, SpecificRecordBase> getProducer() {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, environment.getProperty("spring.kafka.bootstrap-servers"));
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                environment.getProperty("spring.kafka.producer.key-serializer"));
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                environment.getProperty("spring.kafka.producer.value-serializer"));

        return new KafkaProducer<>(config);
    }
}