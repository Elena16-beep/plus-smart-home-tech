package ru.yandex.practicum.client;

import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.model.Action;
import java.time.Instant;

@Slf4j
@Service
public class ScenarioActionProducer {
    private final HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterStub;

    public ScenarioActionProducer(
            @GrpcClient("hub-router") HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterStub) {
        this.hubRouterStub = hubRouterStub;
    }

    public void sendAction(Action action) {
        log.info("Метод sendAction");
        log.info("Action " + action.toString());
        DeviceActionRequest actionRequest = mapToActionRequest(action);
        log.info("Получили actionRequest");

        try {
            Empty response = hubRouterStub.handleDeviceAction(actionRequest);
            log.info("Действие отправлено " + actionRequest);

            if (response.isInitialized()) {
                log.info("Получили ответ от хаба");
            } else {
                log.info("Нет ответа от хаба");
            }
        } catch (RuntimeException e) {
            log.info("Ошибка отправки");
        }
    }

    private DeviceActionRequest mapToActionRequest(Action action) {
        log.info("Метод mapToActionRequest");

        return DeviceActionRequest.newBuilder()
                .setHubId(action.getScenario().getHubId())
                .setScenarioName(action.getScenario().getName())
                .setAction(toDeviceActionProto(action))
                .setTimestamp(setTimestamp())
                .build();
    }

    private ActionTypeProto mapActionType(ActionTypeAvro actionType) {
        log.info("Метод mapActionType " + actionType);

        return switch (actionType) {
            case ACTIVATE -> ActionTypeProto.ACTIVATE;
            case DEACTIVATE -> ActionTypeProto.DEACTIVATE;
            case INVERSE -> ActionTypeProto.INVERSE;
            case SET_VALUE -> ActionTypeProto.SET_VALUE;
        };
    }

    public DeviceActionProto toDeviceActionProto(Action action) {
        log.info("Метод toDeviceActionProto " + action);
        DeviceActionProto.Builder builder = DeviceActionProto.newBuilder()
                .setSensorId(action.getSensor().getId())
                .setType(ActionTypeProto.valueOf(action.getType().toString()));

        if (action.getType() == ActionTypeAvro.SET_VALUE) {
            if (action.getValue() == null) {
                throw new IllegalStateException(
                        "Для действия SET_VALUE должно быть указано значение value для id " + action.getId()
                );
            }

            builder.setValue(action.getValue());
        }

        return builder.build();
    }

    private Timestamp setTimestamp() {
        Instant instant = Instant.now();

        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }
}