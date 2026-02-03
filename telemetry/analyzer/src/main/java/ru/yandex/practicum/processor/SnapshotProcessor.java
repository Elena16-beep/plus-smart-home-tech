package ru.yandex.practicum.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dal.model.Scenario;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.configuration.AnalyzerKafkaConfig;
import ru.yandex.practicum.configuration.ConsumerConfig;
import ru.yandex.practicum.service.HubRouterClient;
import ru.yandex.practicum.service.SnapshotAnalyzer;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class SnapshotProcessor {
    private final Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
    private final KafkaConsumer<String, SensorsSnapshotAvro> consumer;
    private final List<String> topics;
    private final Duration pollTimeout;
    private final SnapshotAnalyzer snapshotAnalyzer;
    private final HubRouterClient hubRouterClient;

    public SnapshotProcessor(AnalyzerKafkaConfig config, SnapshotAnalyzer snapshotAnalyzer, HubRouterClient hubRouterClient) {
        final ConsumerConfig consumerConfig = config.getConsumers().get(this.getClass().getSimpleName());
        this.consumer = new KafkaConsumer<>(consumerConfig.getProperties());
        this.topics = consumerConfig.getTopics();
        this.pollTimeout = consumerConfig.getPollTimeout();
        this.snapshotAnalyzer = snapshotAnalyzer;
        this.hubRouterClient = hubRouterClient;

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Завершение работы консьюмера");
            consumer.wakeup();
        }));
    }

    public void start() {
        try {
            log.trace("Подписка на топики " + topics);
            consumer.subscribe(topics);

            while (true) {
                ConsumerRecords<String, SensorsSnapshotAvro> records = consumer.poll(pollTimeout);
                int count = 0;

                for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                    log.trace("Обработка сообщения от хаба {} partition {} offset {}.",
                            record.key(), record.partition(), record.offset());
                    handleRecord(record.value());
                    manageOffsets(record, count, consumer);
                    count++;
                }

                consumer.commitAsync();
            }
        } catch (WakeupException ignores) {
            log.info("Завершение работы");
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от хабов", e);
        } finally {
            try {
                consumer.commitSync(offsets);
            } finally {
                log.info("Закрытие консьюмера");
                consumer.close();
            }
        }
    }

    private void manageOffsets(ConsumerRecord<String, SensorsSnapshotAvro> record, int count,
                               KafkaConsumer<String, SensorsSnapshotAvro> consumer) {
        offsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1)
        );

        if (count % 100 == 0) {
            consumer.commitAsync(offsets, (committedOffsets, exception) -> {
                if (exception != null) {
                    log.warn("Ошибка во время фиксации оффсетов " + committedOffsets, exception);
                }
            });
        }
    }

    @Transactional
    private void handleRecord(SensorsSnapshotAvro sensorsSnapshotAvro) {
        try {
            String hubId = sensorsSnapshotAvro.getHubId();
            List<Scenario> scenarios = snapshotAnalyzer.analyze(hubId, sensorsSnapshotAvro);

            for (Scenario scenario : scenarios) {
                hubRouterClient.handleScenario(scenario);
            }
        } catch (Exception e) {
            log.error("Ошибка обработки события " + sensorsSnapshotAvro, e);
        }
    }
}