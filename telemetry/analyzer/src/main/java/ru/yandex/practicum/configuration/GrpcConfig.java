package ru.yandex.practicum.configuration;

import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;

@Slf4j
@Service
public class GrpcConfig {
    private final HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    public GrpcConfig(@GrpcClient("hub-router")
                      HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient) {
        this.hubRouterClient = hubRouterClient;
    }

    public void sendDeviceActions(DeviceActionRequest request) {
        log.info("Отправляем событие в хаб: {} для сценария: {}", request.getHubId(), request.getScenarioName());
        hubRouterClient.handleDeviceAction(request);
    }
}