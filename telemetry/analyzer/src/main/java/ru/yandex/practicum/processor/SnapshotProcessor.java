package ru.yandex.practicum.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.service.SnapshotHandler;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor {
    private final KafkaConsumer<String, SensorsSnapshotAvro> snapshotConsumer;
    private final SnapshotHandler snapshotHandler;

    @Value("${analyzer.topic.snapshots-topic}")
    private String snapshotsTopic;

    public void start() {
        try {
            snapshotConsumer.subscribe(List.of(snapshotsTopic));
            Runtime.getRuntime().addShutdownHook(new Thread(snapshotConsumer::wakeup));

            while (true) {
                ConsumerRecords<String, SensorsSnapshotAvro> records =
                        snapshotConsumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                    SensorsSnapshotAvro sensorsSnapshot = record.value();
                    log.info("Получен снимок состояния умного дома " + sensorsSnapshot);
                    snapshotHandler.handleSnapshot(sensorsSnapshot);
                }

                snapshotConsumer.commitSync();
                log.info("Хартбит снимков");
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Ошибка чтения данных из топика " + snapshotsTopic, e);
        } finally {
            try {
                snapshotConsumer.commitSync();
            } finally {
                snapshotConsumer.close();
            }
        }
    }
}