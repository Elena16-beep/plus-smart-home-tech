package ru.yandex.practicum.service;

import com.google.protobuf.Timestamp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dal.model.*;
import ru.yandex.practicum.dal.repository.ScenarioRepository;
import ru.yandex.practicum.grpc.telemetry.event.*;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.kafka.telemetry.event.*;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import static ru.yandex.practicum.dal.model.ConditionType.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScenarioService {
    private final ScenarioRepository scenarioRepository;

    @GrpcClient("hub-router")
    private HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    @Transactional
    public void processSnapshot(SensorsSnapshotAvro sensorsSnapshot) {
        log.warn("SNAPSHOT hubId={}", sensorsSnapshot.getHubId());
        List<Scenario> scenarios = scenarioRepository.findByHubId(sensorsSnapshot.getHubId());

        if (scenarios.isEmpty()) {
            return;
        }

        for (Scenario scenario : scenarios) {
            if (checkConditions(scenario.getConditions(), sensorsSnapshot)) {
                performActions(scenario);
            }
        }
    }

    private boolean checkConditions(Map<String, Condition> conditions, SensorsSnapshotAvro sensorsSnapshot) {
        return conditions.entrySet().stream()
                .allMatch(c -> checkCondition(c.getKey(), c.getValue(), sensorsSnapshot));
    }

    private boolean checkCondition(String sensorId, Condition condition, SensorsSnapshotAvro snapshot) {
        if (!snapshot.getSensorsState().containsKey(sensorId)) {
            return false;
        }

        final SensorStateAvro state = snapshot.getSensorsState().get(sensorId);
        ConditionType type = condition.getType();
        ConditionOperation operation = condition.getOperation();

        return switch (state.getData()) {
            case ClimateSensorAvro data -> switch (type) {
                case TEMPERATURE -> this.check(operation, data.getTemperatureC(), condition.getValue());
                case CO2LEVEL -> this.check(operation, data.getCo2Level(), condition.getValue());
                case HUMIDITY -> this.check(operation, data.getHumidity(), condition.getValue());
                default -> false;
            };
            case LightSensorAvro data ->
                    type.equals(LUMINOSITY) && this.check(operation, data.getLuminosity(), condition.getValue());
            case MotionSensorAvro data ->
                    type.equals(MOTION) && this.check(operation, data.getMotion() ? 1 : 0, condition.getValue());
            case TemperatureSensorAvro data ->
                    type.equals(TEMPERATURE) && this.check(operation, data.getTemperatureC(), condition.getValue());
            case SwitchSensorAvro data ->
                    type.equals(SWITCH) && this.check(operation, data.getState() ? 1 : 0, condition.getValue());
            default -> false;
        };
    }

    public boolean check(ConditionOperation conditionOperation, Integer sensorValue, Integer expectedValue) {
        if (sensorValue == null || expectedValue == null) return false;

        return switch (conditionOperation) {
            case EQUALS -> sensorValue.equals(expectedValue);
            case GREATER_THAN -> sensorValue > expectedValue;
            case LOWER_THAN -> sensorValue < expectedValue;
        };
    }


    private void performActions(Scenario scenario) {
        log.debug("Starting actions Scenario {}, Hub {}", scenario.getName(), scenario.getHubId());

        for (Map.Entry<String, Action> actions : scenario.getActions().entrySet()) {
            Action action = actions.getValue();
            DeviceActionProto.Builder deviceAction = DeviceActionProto.newBuilder()
                    .setSensorId(actions.getKey())
                    .setType(this.mapActionType(action.getType()));
            if (action.getType().equals(ActionTypeAvro.SET_VALUE)) {
                deviceAction.setValue(action.getValue());
            }
            try {
                Instant now = Instant.now();
                Timestamp timestamp = Timestamp.newBuilder()
                        .setSeconds(now.getEpochSecond())
                        .setNanos(now.getNano())
                        .build();
                hubRouterClient.handleDeviceAction(DeviceActionRequest.newBuilder()
                        .setHubId(scenario.getHubId())
                        .setScenarioName(scenario.getName())
                        .setAction(deviceAction.build())
                        .setTimestamp(timestamp)
                        .build()
                );
            } catch (Exception ex) {
                log.error("Exception with Action {}, Hub {}, Device {}",
                        action.getType().name(), scenario.getHubId(), action.getId(), ex);
            }
        }
    }

    private ActionTypeProto mapActionType(ActionType avro) {
        for (ActionTypeProto value : ActionTypeProto.values()) {
            if (value.name().equalsIgnoreCase(avro.name())) {
                return value;
            }
        }
        return null;
    }
}