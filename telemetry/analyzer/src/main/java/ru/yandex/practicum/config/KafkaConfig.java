package ru.yandex.practicum.config;

import lombok.RequiredArgsConstructor;
//import org.apache.avro.specific.SpecificRecordBase;
//import org.apache.kafka.clients.producer.KafkaProducer;
//import org.apache.kafka.clients.producer.Producer;
//import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.core.env.Environment;
//import ru.yandex.practicum.kafka.deserializer.SnapshotDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
//import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {
//    @Value("${kafka.bootstrap-servers}")
//    private String bootstrapServers;
//
//    @Value("${kafka.snapshot-consumer-properties.group-id}")
//    private String snapshotGroupId;
//
//    @Value("${kafka.hub-consumer-properties.group-id}")
//    private String hubGroupId;
//
//    @Value("${kafka.key-deserializer}")
//    private String keyDeserializer;
//
//    @Value("${kafka.hub-consumer-properties.value-deserializer}")
//    private String hubValueDeserializer;

    private final Environment environment;

    @Bean
    public KafkaConsumer<String, SensorsSnapshotAvro> snapshotsConsumer() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, environment.getProperty("spring.kafka.bootstrap-servers"));
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "spring.kafka.consumer.snapshots.group-id");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, environment.getProperty("spring.kafka.consumer.snapshots.key-deserializer"));
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "spring.kafka.consumer.snapshots.value-deserializer");

        return new KafkaConsumer<>(properties);
    }

    @Bean
    public KafkaConsumer<String, HubEventAvro> hubsConsumer() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, environment.getProperty("spring.kafka.bootstrap-servers"));
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "spring.kafka.consumer.hub.group-id");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, environment.getProperty("spring.kafka.consumer.hub.key-deserializer"));
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, environment.getProperty("spring.kafka.consumer.hub.value-deserializer"));

        return new KafkaConsumer<>(properties);
    }

//    private final Environment environment;
//
//    @Bean
//    public Producer<String, SpecificRecordBase> getProducer() {
//        Properties config = new Properties();
//        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, environment.getProperty("spring.kafka.bootstrap-servers"));
//        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, environment.getProperty("spring.kafka.producer.key-serializer"));
//        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, environment.getProperty("spring.kafka.producer.value-serializer"));
//
//        return new KafkaProducer<>(config);
//    }
}