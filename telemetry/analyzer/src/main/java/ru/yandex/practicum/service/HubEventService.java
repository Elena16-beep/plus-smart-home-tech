//package ru.yandex.practicum.service;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import ru.yandex.practicum.dal.model.*;
//import ru.yandex.practicum.dal.repository.ActionRepository;
//import ru.yandex.practicum.dal.repository.ConditionRepository;
//import ru.yandex.practicum.dal.repository.ScenarioRepository;
//import ru.yandex.practicum.dal.repository.SensorRepository;
//import ru.yandex.practicum.kafka.telemetry.event.*;
//import java.util.ArrayList;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class HubEventService {
//
//    private final SensorRepository sensorRepository;
//    private final ScenarioRepository scenarioRepository;
//    private final ConditionRepository conditionRepository;
//    private final ActionRepository actionRepository;
//
//    @Transactional
//    public void processEvent(HubEventAvro hubEvent) {
//        if (hubEvent == null || hubEvent.getPayload() == null || hubEvent.getHubId() == null) {
//            return;
//        }
//
//        String hubId = hubEvent.getHubId();
//        Object payload = hubEvent.getPayload();
//
//        switch (payload) {
//            case DeviceAddedEventAvro e -> handleDeviceAdded(hubId, e);
//            case DeviceRemovedEventAvro e -> handleDeviceRemoved(hubId, e);
//            case ScenarioAddedEventAvro e -> handleScenarioAdded(hubId, e);
//            case ScenarioRemovedEventAvro e -> handleScenarioRemoved(hubId, e);
//            default -> log.warn("Unknown payload type: {}", payload.getClass());
//        }
//    }
//
//    private void handleDeviceAdded(String hubId, DeviceAddedEventAvro event) {
//        sensorRepository.findByIdAndHubId(event.getId(), hubId)
//                .orElseGet(() -> sensorRepository.save(
//                        Sensor.builder()
//                                .id(event.getId())
//                                .hubId(hubId)
//                                .build()));
//        log.info("Device added: hub={}, sensor={}", hubId, event.getId());
//    }
//
//    private void handleDeviceRemoved(String hubId, DeviceRemovedEventAvro event) {
//        sensorRepository.findByIdAndHubId(event.getId(), hubId)
//                .ifPresent(sensorRepository::delete);
//        log.info("Device removed: hub={}, sensor={}", hubId, event.getId());
//    }
//
//    private void handleScenarioAdded(String hubId, ScenarioAddedEventAvro event) {
//
//        Scenario scenario = scenarioRepository.findByHubIdAndName(hubId, event.getName())
//                .orElseGet(() -> Scenario.builder()
//                        .hubId(hubId)
//                        .name(event.getName())
//                        .build());
//
//        actionRepository.deleteAll(new ArrayList<>(scenario.getActions().values()));
//        scenario.getActions().clear();
//
//        conditionRepository.deleteAll(new ArrayList<>(scenario.getConditions().values()));
//        scenario.getConditions().clear();
//
//        event.getConditions().forEach(c -> {
//            Condition cond = Condition.builder()
//                    .type(ConditionType.valueOf(c.getType().name()))
//                    .operation(ConditionOperation.valueOf(c.getOperation().name()))
//                    .value(convertValue(c.getValue()))
//                    .build();
//
//            scenario.addCondition(c.getSensorId(), cond);
//        });
//
//        event.getActions().forEach(a -> {
//            Action action = Action.builder()
//                    .type(ActionType.valueOf(a.getType().name()))
//                    .build();
//            if (a.getType() == ActionTypeAvro.SET_VALUE) {
//                action.setValue(convertValue(a.getValue()));
//            }
//            scenario.addAction(a.getSensorId(), action);
//        });
//
//        conditionRepository.saveAll(scenario.getConditions().values());
//        actionRepository.saveAll(scenario.getActions().values());
//
//        scenarioRepository.save(scenario);
//
//        log.info("Scenario added: hub={}, name={}", hubId, event.getName());
//    }
//
//    private void handleScenarioRemoved(String hubId, ScenarioRemovedEventAvro event) {
//        scenarioRepository.findByHubIdAndName(hubId, event.getName())
//                .ifPresent(s -> {
//                    conditionRepository.deleteAll(s.getConditions().values());
//                    actionRepository.deleteAll(s.getActions().values());
//                    scenarioRepository.delete(s);
//                });
//
//        log.info("Scenario removed: hub={}, name={}", hubId, event.getName());
//    }
//
//    private Integer convertValue(Object value) {
//        return switch (value) {
//            case Integer i -> i;
//            case Boolean b -> b ? 1 : 0;
//            default -> null;
//        };
//    }
//}