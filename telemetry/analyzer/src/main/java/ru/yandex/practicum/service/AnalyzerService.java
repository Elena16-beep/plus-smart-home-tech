//package ru.yandex.practicum.service;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.kafka.clients.consumer.ConsumerRecords;
//import org.springframework.stereotype.Service;
//import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
//import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
//import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
//import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
//import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class AnalyzerService {
//    private final HubEventStore hubEventStore;
//
//    public void saveHubEvent(ConsumerRecords<String, HubEventAvro> hubEventList) {
//        hubEventList.forEach(record -> {
//            HubEventAvro event = record.value();
//            Object payload = event.getPayload();
//
//            if (payload instanceof DeviceAddedEventAvro deviceAdded) {
//                hubEventStore.saveDevice(deviceAdded.getId(), event.getHubId());
//                log.info("Сенсор {} успешно добавлен в хаб {}!", deviceAdded.getId(), event.getHubId());
//            }
//
//            if (payload instanceof ScenarioAddedEventAvro scenarioAdded) {
//                hubEventStore.saveScenario(event, scenarioAdded);
//                log.info("Сценарий {} успешно добавлен!", scenarioAdded.getName());
//            }
//
//            if (payload instanceof DeviceRemovedEventAvro deviceRemoved) {
//                hubEventStore.removeDevice(deviceRemoved.getId(), event.getHubId());
//                log.info("Сенсор {} успешно удален!", deviceRemoved.getId());
//            }
//
//            if (payload instanceof ScenarioRemovedEventAvro scenarioRemoved) {
//                hubEventStore.removeScenario(scenarioRemoved.getName(), event.getHubId());
//                log.info("Сценарий {} успешно удален!", scenarioRemoved.getName());
//            }
//        });
//    }
//}