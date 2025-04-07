package practicum.service;

import practicum.model.Action;

public interface HubActionProducer {

    void sendAction(String hubId, String scenarioName, String sensorId, Action action);

}
