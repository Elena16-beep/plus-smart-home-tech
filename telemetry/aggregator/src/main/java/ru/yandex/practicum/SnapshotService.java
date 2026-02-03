package ru.yandex.practicum;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class SnapshotService {
    private final Map<String, SensorsSnapshotAvro> snapshotAvroMap = new HashMap<>();

    Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        SensorsSnapshotAvro snapshot =
                snapshotAvroMap.getOrDefault(event.getHubId(),
                        SensorsSnapshotAvro.newBuilder()
                                .setHubId(event.getHubId())
                                .setTimestamp(event.getTimestamp())
                                .setSensorsState(new HashMap<>())
                                .build());

        SensorStateAvro oldState = snapshot.getSensorsState().get(event.getId());

        if (oldState != null &&
                (oldState.getTimestamp().isAfter(event.getTimestamp()) ||
                        oldState.getData().equals(event.getPayload()))) {
            return Optional.empty();
        }

        SensorStateAvro newState =
                SensorStateAvro.newBuilder()
                        .setTimestamp(event.getTimestamp())
                        .setData(event.getPayload())
                        .build();
        snapshot.getSensorsState().put(event.getId(), newState);
        snapshot.setTimestamp(event.getTimestamp());
        snapshotAvroMap.put(event.getHubId(), snapshot);

        return Optional.of(snapshot);
    }
}