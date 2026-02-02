package ru.yandex.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.*;
import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.hub.*;
import java.time.Instant;
import java.util.Optional;

@Component
public class HubEventProtoMapper {
    public HubEvent toJava(HubEventProto hubEventProto) {
        final Instant timestamp = Optional.of(hubEventProto.getTimestamp())
                .map(ts -> Instant.ofEpochSecond(ts.getSeconds(), ts.getNanos()))
                .orElse(Instant.now());

        return switch (hubEventProto.getPayloadCase()) {
            case DEVICE_ADDED -> {
                var hubEvent = hubEventProto.getDeviceAdded();
                yield DeviceAddedEvent.builder()
                        .id(hubEvent.getId())
                        .hubId(hubEventProto.getHubId())
                        .timestamp(timestamp)
                        .deviceType(mapDeviceType(hubEvent.getType()))
                        .build();
            }
            case DEVICE_REMOVED -> {
                var hubEvent = hubEventProto.getDeviceRemoved();
                yield DeviceRemovedEvent.builder()
                        .id(hubEvent.getId())
                        .hubId(hubEventProto.getHubId())
                        .timestamp(timestamp)
                        .build();
            }
            case SCENARIO_ADDED -> {
                var hubEvent = hubEventProto.getScenarioAdded();
                yield ScenarioAddedEvent.builder()
                        .hubId(hubEventProto.getHubId())
                        .timestamp(timestamp)
                        .name(hubEvent.getName())
                        .conditions(hubEvent.getConditionList().stream()
                                .map(this::mapCondition)
                                .toList())
                        .actions(hubEvent.getActionList().stream()
                                .map(this::mapAction)
                                .toList())
                        .build();
            }
            case SCENARIO_REMOVED -> {
                var hubEvent = hubEventProto.getScenarioRemoved();
                yield ScenarioRemovedEvent.builder()
                        .hubId(hubEventProto.getHubId())
                        .timestamp(timestamp)
                        .name(hubEvent.getName())
                        .build();
            }
            default -> throw new IllegalArgumentException("Неизвестный тип события хаба " + hubEventProto.getPayloadCase());
        };
    }

    private ScenarioCondition mapCondition(ScenarioConditionProto scenarioConditionProto) {
        Integer value =
                switch (scenarioConditionProto.getValueCase()) {
                    case BOOL_VALUE -> scenarioConditionProto.getBoolValue() ? 1 : 0;
                    case INT_VALUE -> scenarioConditionProto.getIntValue();
                    default -> null;
                };

        return ScenarioCondition.builder()
                .sensorId(scenarioConditionProto.getSensorId())
                .type(mapConditionType(scenarioConditionProto.getType()))
                .operation(mapOperation(scenarioConditionProto.getOperation()))
                .value(value)
                .build();
    }

    private DeviceAction mapAction(DeviceActionProto deviceActionProto) {
        return DeviceAction.builder()
                .sensorId(deviceActionProto.getSensorId())
                .type(mapActionType(deviceActionProto.getType()))
                .value(deviceActionProto.hasValue() ? deviceActionProto.getValue() : null)
                .build();
    }

    private DeviceType mapDeviceType(DeviceTypeProto deviceTypeProto) {
        return switch (deviceTypeProto) {
            case MOTION_SENSOR -> DeviceType.MOTION_SENSOR;
            case TEMPERATURE_SENSOR -> DeviceType.TEMPERATURE_SENSOR;
            case LIGHT_SENSOR -> DeviceType.LIGHT_SENSOR;
            case CLIMATE_SENSOR -> DeviceType.CLIMATE_SENSOR;
            case SWITCH_SENSOR -> DeviceType.SWITCH_SENSOR;
            default -> throw new IllegalArgumentException("Неизвестный тип " + deviceTypeProto);
        };
    }

    private ScenarioConditionType mapConditionType(ConditionTypeProto conditionTypeProto) {
        return switch (conditionTypeProto) {
            case MOTION -> ScenarioConditionType.MOTION;
            case LUMINOSITY -> ScenarioConditionType.LUMINOSITY;
            case SWITCH -> ScenarioConditionType.SWITCH;
            case TEMPERATURE -> ScenarioConditionType.TEMPERATURE;
            case CO2LEVEL -> ScenarioConditionType.CO2LEVEL;
            case HUMIDITY -> ScenarioConditionType.HUMIDITY;
            default -> throw new IllegalArgumentException("Неизвестный тип " + conditionTypeProto);
        };
    }

    private DeviceActionType mapActionType(ActionTypeProto actionTypeProto) {
        return switch (actionTypeProto) {
            case ACTIVATE -> DeviceActionType.ACTIVATE;
            case DEACTIVATE -> DeviceActionType.DEACTIVATE;
            case INVERSE -> DeviceActionType.INVERSE;
            case SET_VALUE -> DeviceActionType.SET_VALUE;
            default -> throw new IllegalArgumentException("Неизвестный тип " + actionTypeProto);
        };
    }

    private ConditionOperation mapOperation(ConditionOperationProto conditionOperationProto) {
        return switch (conditionOperationProto) {
            case EQUALS -> ConditionOperation.EQUALS;
            case GREATER_THAN -> ConditionOperation.GREATER_THAN;
            case LOWER_THAN -> ConditionOperation.LOWER_THAN;
            default -> throw new IllegalArgumentException("Неизвестный тип " + conditionOperationProto);
        };
    }
}
