package ru.yandex.practicum.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.service.ScenarioService;
import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor {

    @Value("${app.topics.snapshots}")
    private String snapshotsTopic;

    private final ScenarioService scenarioService;
    private final KafkaConsumer<String, SensorsSnapshotAvro> snapshotConsumer;

    public void start() {
        try {
            log.info("Подписка на топик " + snapshotsTopic);
            snapshotConsumer.subscribe(List.of(snapshotsTopic));

            while (true) {
                var records = snapshotConsumer.poll(Duration.ofSeconds(1));

                for (var rec : records) {
                    SensorsSnapshotAvro snapshot = rec.value();

                    if (snapshot != null) {
                        scenarioService.processSnapshot(snapshot);
                    }
                }

                snapshotConsumer.commitAsync();
            }

        } catch (WakeupException e) {
            log.info("Завершение работы");
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от хабов", e);
        } finally {
            try {
                snapshotConsumer.commitSync();
            } catch (Exception ignore) {
            }
            snapshotConsumer.close();
            log.info("Закрытие консьюмера");
        }
    }
}