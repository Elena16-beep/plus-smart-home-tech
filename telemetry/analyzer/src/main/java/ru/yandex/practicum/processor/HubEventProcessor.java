package ru.yandex.practicum.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.service.HubEventService;
import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable {

    @Value("${app.topics.hubs}")
    private String hubsTopic;

    private final KafkaConsumer<String, HubEventAvro> hubEventConsumer;
    private final HubEventService hubEventService;

    @Override
    public void run() {
        try {
            log.info("Подписка на топик " + hubsTopic);
            hubEventConsumer.subscribe(List.of(hubsTopic));

            while (true) {
                var records = hubEventConsumer.poll(Duration.ofSeconds(1));

                if (!records.isEmpty()) {
                    records.forEach(record ->
                            hubEventService.processEvent(record.value())
                    );
                }
            }

        } catch (WakeupException e) {
            log.info("Завершение работы");
        } catch (Exception e) {
            log.error("Ошибка во время обработки сценариев от хабов", e);
        } finally {
            try {
                hubEventConsumer.close();
                log.info("Закрытие консьюмера");
            } catch (Exception e) {
                log.error("Ошибка при закрытии ", e);
            }
        }
    }
}