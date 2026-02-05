package ru.yandex.practicum.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.service.HubEventHandler;
import ru.yandex.practicum.service.HubEventHandlers;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable {
    private final KafkaConsumer<String, HubEventAvro> hubConsumer;
    private final HubEventHandlers handlers;

    @Value("${analyzer.topic.hub-event-topic}")
    private String hubsTopic;

    @Override
    public void run() {
        try {
            hubConsumer.subscribe(List.of(hubsTopic));
            Runtime.getRuntime().addShutdownHook(new Thread(hubConsumer::wakeup));
            Map<String, HubEventHandler> handlerMap = handlers.getHandlers();

            while (true) {
                ConsumerRecords<String, HubEventAvro> records = hubConsumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    HubEventAvro event = record.value();
                    String payloadName = event.getPayload().getClass().getSimpleName();
                    log.info("Получено сообщение хаба " + payloadName);

                    if (handlerMap.containsKey(payloadName)) {
                        handlerMap.get(payloadName).handle(event);
                    } else {
                        throw new IllegalArgumentException("Не найден обработчик для события " + event);
                    }
                }

                hubConsumer.commitSync();
                log.info("Хартбит хабов");
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Ошибка чтения данных из топика " + hubsTopic, e);
        } finally {
            try {
                hubConsumer.commitSync();
            } finally {
                log.info("Закрытие Consumer");
                hubConsumer.close();
            }
        }
    }
}