package practicum.service;

import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

public interface EventService {

    void collectSensorEvent(SensorEventAvro sensorEventAvro);

    void collectHubEvent(HubEventAvro hubEventAvro);

}
