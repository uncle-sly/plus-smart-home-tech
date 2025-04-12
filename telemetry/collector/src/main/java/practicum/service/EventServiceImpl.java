package practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import practicum.kafka.KafkaProducer;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    @Value("${kafka.topic.sensor}")
    private String sensorTopic;
    @Value("${kafka.topic.hub}")
    private String hubTopic;

    private final KafkaProducer kafkaProducer;

    @Override
    public void collectSensorEvent(SensorEventAvro sensorEventAvro) {
        log.info("Collecting sensor event");
        kafkaProducer.getProducer().send(kafkaProducer.getProducerRecord(sensorTopic, sensorEventAvro));
        log.info("Collected sensor event");
        log.info("Sensor Kafka_topic: {}", sensorTopic);
        log.info("Sensor event: {}", sensorEventAvro);
    }

    @Override
    public void collectHubEvent(HubEventAvro hubEventAvro) {
        log.info("Collecting hub event");
        kafkaProducer.getProducer().send(kafkaProducer.getProducerRecord(hubTopic, hubEventAvro));
        log.info("Collected hub event");
        log.info("Hub Kafka_topic: {}", hubTopic);
        log.info("Hub event: {}", hubEventAvro);
    }

}