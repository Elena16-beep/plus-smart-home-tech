package ru.yandex.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import java.time.Duration;
import java.util.List;

@Slf4j
@Component
public class AggregationStarter {
    private static final Duration CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(1000);

    @Value("${kafka.topics.snapshots}")
    private String snapshotsTopic;
    @Value("${kafka.topics.sensors}")
    private String sensorsTopic;

    private final KafkaProducer<String, SensorsSnapshotAvro> kafkaProducer;
    private final KafkaConsumer<String, SensorEventAvro> kafkaConsumer;
    private final SnapshotService snapshotService;

    public AggregationStarter(
            KafkaProducer<String, SensorsSnapshotAvro> kafkaProducer,
            KafkaConsumer<String, SensorEventAvro> kafkaConsumer,
            SnapshotService snapshotService) {
        this.kafkaProducer = kafkaProducer;
        this.kafkaConsumer = kafkaConsumer;
        this.snapshotService = snapshotService;
    }

    public void start() {
        Runtime.getRuntime().addShutdownHook(new Thread(kafkaConsumer::wakeup));
        try {
            kafkaConsumer.subscribe(List.of(sensorsTopic));
            while (true) {
                ConsumerRecords<String, SensorEventAvro> records = kafkaConsumer.poll(CONSUME_ATTEMPT_TIMEOUT);
                for (ConsumerRecord<String, SensorEventAvro> record : records) {
                    snapshotService.updateState(record.value())
                            .ifPresent(snapshotAvro ->
                                    kafkaProducer.send(new ProducerRecord<>(snapshotsTopic, snapshotAvro)));
                }
                kafkaConsumer.commitSync();
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий ", e);
        } finally {
            try {
                kafkaProducer.flush();
                kafkaConsumer.commitSync();
            } finally {
                kafkaConsumer.close();
                kafkaProducer.close();
            }
        }
    }
}