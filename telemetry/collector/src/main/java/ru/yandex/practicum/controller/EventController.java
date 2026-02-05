package ru.yandex.practicum.controller;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.mapper.HubEventProtoMapper;
import ru.yandex.practicum.mapper.SensorEventProtoMapper;
import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.service.EventService;

@GrpcService
public class EventController extends CollectorControllerGrpc.CollectorControllerImplBase {
    private final EventService eventService;
    private final HubEventProtoMapper hubEventProtoMapper;
    private final SensorEventProtoMapper sensorEventProtoMapper;

    public EventController(EventService eventService, HubEventProtoMapper hubEventProtoMapper, SensorEventProtoMapper sensorEventProtoMapper) {
        this.eventService = eventService;
        this.hubEventProtoMapper = hubEventProtoMapper;
        this.sensorEventProtoMapper = sensorEventProtoMapper;
    }

    @Override
    public void collectHubEvent(HubEventProto hubEventProto, StreamObserver<Empty> responseObserver) {
        try {
            HubEvent hubEvent = hubEventProtoMapper.toJava(hubEventProto);

            if (hubEvent.getHubId() == null || hubEvent.getHubId().isEmpty()) {
                responseObserver.onError(new StatusRuntimeException(
                        Status.INVALID_ARGUMENT.
                                withDescription("hubId не найден")));

                return;
            }

            eventService.createHubEvent(hubEvent);
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL.
                            withDescription(e.getLocalizedMessage()).
                            withCause(e)));
        }
    }

    @Override
    public void collectSensorEvent(SensorEventProto sensorEventProto, StreamObserver<Empty> responseObserver) {
        try {
            SensorEvent sensorEvent = sensorEventProtoMapper.toJava(sensorEventProto);

            if (sensorEvent.getHubId() == null || sensorEvent.getHubId().isEmpty()) {
                responseObserver.onError(new StatusRuntimeException(
                        Status.INVALID_ARGUMENT.
                                withDescription("hubId не найден")));

                return;
            }

            eventService.createSensorEvent(sensorEvent);
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL.
                            withDescription(e.getLocalizedMessage()).
                            withCause(e)));
        }
    }
}