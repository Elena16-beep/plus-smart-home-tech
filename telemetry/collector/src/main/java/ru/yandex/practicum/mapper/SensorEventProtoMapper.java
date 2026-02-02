package ru.yandex.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.model.sensor.*;
import java.time.Instant;
import java.util.Optional;

@Component
public class SensorEventProtoMapper {
    public SensorEvent toJava(SensorEventProto sensorEventProto) {
        final Instant timestamp = Optional.of(sensorEventProto.getTimestamp())
                .map(tsProto -> Instant.ofEpochSecond(tsProto.getSeconds(), tsProto.getNanos()))
                .orElse(Instant.now());

        return switch (sensorEventProto.getPayloadCase()) {
            case MOTION_SENSOR -> {
                var sensorEvent = sensorEventProto.getMotionSensor();
                yield MotionSensorEvent.builder()
                        .id(sensorEventProto.getId())
                        .hubId(sensorEventProto.getHubId())
                        .timestamp(timestamp)
                        .linkQuality(sensorEvent.getLinkQuality())
                        .motion(sensorEvent.getMotion())
                        .voltage(sensorEvent.getVoltage())
                        .build();
            }
            case TEMPERATURE_SENSOR -> {
                var sensorEvent = sensorEventProto.getTemperatureSensor();
                yield TemperatureSensorEvent.builder()
                        .id(sensorEventProto.getId())
                        .hubId(sensorEventProto.getHubId())
                        .timestamp(timestamp)
                        .temperatureC(sensorEvent.getTemperatureC())
                        .temperatureF(sensorEvent.getTemperatureF())
                        .build();
            }
            case LIGHT_SENSOR -> {
                var sensorEvent = sensorEventProto.getLightSensor();
                yield LightSensorEvent.builder()
                        .id(sensorEventProto.getId())
                        .hubId(sensorEventProto.getHubId())
                        .timestamp(timestamp)
                        .linkQuality(sensorEvent.getLinkQuality())
                        .luminosity(sensorEvent.getLuminosity())
                        .build();
            }
            case CLIMATE_SENSOR -> {
                var sensorEvent = sensorEventProto.getClimateSensor();
                yield ClimateSensorEvent.builder()
                        .id(sensorEventProto.getId())
                        .hubId(sensorEventProto.getHubId())
                        .timestamp(timestamp)
                        .temperatureC(sensorEvent.getTemperatureC())
                        .humidity(sensorEvent.getHumidity())
                        .co2Level(sensorEvent.getCo2Level())
                        .build();
            }
            case SWITCH_SENSOR -> {
                var sensorEvent = sensorEventProto.getSwitchSensor();
                yield SwitchSensorEvent.builder()
                        .id(sensorEventProto.getId())
                        .hubId(sensorEventProto.getHubId())
                        .timestamp(timestamp)
                        .state(sensorEvent.getState())
                        .build();
            }
            default ->
                    throw new IllegalArgumentException("Неизвестный тип события датчиков " + sensorEventProto.getPayloadCase());
        };
    }
}