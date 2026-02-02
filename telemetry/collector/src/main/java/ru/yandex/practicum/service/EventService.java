package ru.yandex.practicum.service;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.configuration.KafkaProducerProperties;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.mapper.HubEventAvroMapper;
import ru.yandex.practicum.mapper.SensorEventAvroMapper;
import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.sensor.SensorEvent;

@Service
public class EventService {
    private final KafkaProducerProperties kafkaProducerProperties;
    private final Producer<String, SpecificRecordBase> producer;

    public EventService(Producer<String, SpecificRecordBase> producer, KafkaProducerProperties properties) {
        this.producer = producer;
        this.kafkaProducerProperties = properties;
    }

    public void createSensorEvent(SensorEvent sensorEvent) {
        SensorEventAvro sensorEventAvro = SensorEventAvroMapper.toSensorEventAvro(sensorEvent);
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
                kafkaProducerProperties.getTopics().getSensors(),
                null,
                sensorEvent.getTimestamp().toEpochMilli(),
                sensorEvent.getHubId(),
                sensorEventAvro
        );

        producer.send(record);
    }

    public void createHubEvent(HubEvent hubEvent) {
        HubEventAvro hubEventAvro = HubEventAvroMapper.toHubEventAvro(hubEvent);
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
                kafkaProducerProperties.getTopics().getHubs(),
                null,
                hubEvent.getTimestamp().toEpochMilli(),
                hubEvent.getHubId(),
                hubEventAvro
        );

        producer.send(record);
    }
}