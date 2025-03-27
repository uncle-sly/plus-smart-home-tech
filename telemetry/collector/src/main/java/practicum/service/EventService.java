package practicum.service;

import practicum.model.hub.HubEvent;
import practicum.model.sensor.SensorEvent;

public interface EventService {

    void collectSensorEvent(SensorEvent sensorEvent, String kafka_topic);

    void collectHubEvent(HubEvent hubEvent, String kafka_topic);

}
