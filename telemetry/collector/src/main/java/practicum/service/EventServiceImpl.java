package practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import practicum.kafka.KafkaProducer;
import practicum.mapper.HubEventMapper;
import practicum.mapper.SensorEventMapper;
import practicum.model.hub.HubEvent;
import practicum.model.sensor.SensorEvent;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final KafkaProducer kafkaProducer;

    @Override
    public void collectSensorEvent(SensorEvent sensorEvent, String kafka_topic) {
        log.info("Collecting sensor event");
        kafkaProducer.getProducer().send(kafkaProducer.getProducerRecord(kafka_topic, SensorEventMapper.toSensorEventAvro(sensorEvent)));
        log.info("Collected sensor event");
        log.info("Sensor Kafka_topic: {}", kafka_topic);
        log.info("Sensor event: {}", sensorEvent);
    }

    @Override
    public void collectHubEvent(HubEvent hubEvent, String kafka_topic) {
        log.info("Collecting hub event");
        kafkaProducer.getProducer().send(kafkaProducer.getProducerRecord(kafka_topic, HubEventMapper.toHubEventAvro(hubEvent)));
        log.info("Collected hub event");
        log.info("Hub Kafka_topic: {}", kafka_topic);
        log.info("Hub event: {}", hubEvent);
    }

}