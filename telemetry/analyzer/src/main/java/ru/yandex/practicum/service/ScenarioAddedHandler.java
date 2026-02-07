package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.model.Action;
import ru.yandex.practicum.model.Condition;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.model.Sensor;
import ru.yandex.practicum.repository.ActionRepository;
import ru.yandex.practicum.repository.ConditionRepository;
import ru.yandex.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.repository.SensorRepository;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScenarioAddedHandler implements HubEventHandler {
    private final ScenarioRepository scenarioRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;
    private final SensorRepository sensorRepository;

    @Override
    @Transactional
    public void handle(HubEventAvro event) {
        ScenarioAddedEventAvro scenarioAddedEvent = (ScenarioAddedEventAvro) event.getPayload();
        String hubId = event.getHubId();
        Scenario scenario;
        Optional<Scenario> scenarioOpt = scenarioRepository.findByHubIdAndName(hubId,
                scenarioAddedEvent.getName());

        if (scenarioOpt.isEmpty()) {
            scenario = scenarioRepository.save(mapToScenario(event));
        } else {
            scenario = scenarioOpt.get();
        }

        Set<Condition> conditions = mapToConditions(scenarioAddedEvent, scenario, hubId);
        Set<Action> actions = mapToActions(scenarioAddedEvent, scenario, hubId);

        if (checkSensorsInScenarioConditions(scenarioAddedEvent, hubId) && !conditions.isEmpty()) {
            conditionRepository.saveAll(conditions);
        }

        if (checkSensorsInScenarioActions(scenarioAddedEvent, hubId) && !actions.isEmpty()) {
            actionRepository.saveAll(actions);
        }
    }

    @Override
    public String getPayloadType() {
        return ScenarioAddedEventAvro.class.getSimpleName();
    }

    private Scenario mapToScenario(HubEventAvro event) {
        ScenarioAddedEventAvro scenarioAddedEvent = (ScenarioAddedEventAvro) event.getPayload();

        return Scenario.builder()
                .name(scenarioAddedEvent.getName())
                .hubId(event.getHubId())
                .build();
    }

    private Set<Condition> mapToConditions(ScenarioAddedEventAvro scenarioAddedEvent, Scenario scenario, String hubId) {
        log.info("Обработка состояний " + scenarioAddedEvent.getConditions());

        return scenarioAddedEvent.getConditions().stream()
                .map(condition -> {
                    Optional<Sensor> sensorOpt = sensorRepository.findByIdAndHubId(condition.getSensorId(), hubId);

                    if (sensorOpt.isEmpty()) {
                        log.warn("Датчик {} не найден для хаба {} по сценарию {}",
                                condition.getSensorId(), hubId, scenario.getName());

                        return null;
                    }

                    return Condition.builder()
                            .sensor(sensorOpt.get())
                            .scenario(scenario)
                            .type(condition.getType())
                            .operation(condition.getOperation())
                            .value(setValue(condition.getValue()))
                            .build();
                })
                .collect(Collectors.toSet());
    }

    private Set<Action> mapToActions(ScenarioAddedEventAvro scenarioAddedEvent, Scenario scenario, String hubId) {
        log.info("Обработка списка действий " + scenarioAddedEvent.getActions());

        return scenarioAddedEvent.getActions().stream()
                .map(action -> {
                    Optional<Sensor> sensorOpt = sensorRepository.findByIdAndHubId(action.getSensorId(), hubId);

                    if (sensorOpt.isEmpty()) {
                        log.warn("Датчик {} не найден для хаба {} по действию {}",
                                action.getSensorId(), hubId, scenario.getName());

                        return null;
                    }

                    return Action.builder()
                            .sensor(sensorOpt.get())
                            .scenario(scenario)
                            .type(action.getType())
                            .value(action.getValue())
                            .build();
                })
                .collect(Collectors.toSet());
    }

    private Integer setValue(Object value) {
        if (value instanceof Integer) {
            return (Integer) value;
        } else {
            return (Boolean) value ? 1 : 0;
        }
    }

    private boolean checkSensorsInScenarioConditions(ScenarioAddedEventAvro scenarioAddedEvent, String hubId) {
        return sensorRepository.existsByIdInAndHubId(scenarioAddedEvent.getConditions().stream()
                .map(ScenarioConditionAvro::getSensorId)
                .toList(), hubId);
    }

    private boolean checkSensorsInScenarioActions(ScenarioAddedEventAvro scenarioAddedEvent, String hubId) {
        return sensorRepository.existsByIdInAndHubId(scenarioAddedEvent.getActions().stream()
                .map(DeviceActionAvro::getSensorId)
                .toList(), hubId);
    }
}