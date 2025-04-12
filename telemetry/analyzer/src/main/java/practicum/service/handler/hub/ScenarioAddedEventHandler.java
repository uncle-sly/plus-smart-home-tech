package practicum.service.handler.hub;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import practicum.model.Action;
import practicum.model.Condition;
import practicum.model.Scenario;
import practicum.model.Sensor;
import practicum.repository.ScenarioRepository;
import practicum.repository.SensorRepository;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
@Component
public class ScenarioAddedEventHandler implements HubEventHandler {

    private final ScenarioRepository scenarioRepository;
    private final SensorRepository sensorRepository;

    @Override
    @Transactional
    public void handle(HubEventAvro hubEventAvro) {

        if (!(hubEventAvro.getPayload() instanceof ScenarioAddedEventAvro payload)) {
            log.warn("Получено событие неизвестного типа: {}", hubEventAvro.getPayload().getClass().getName());
            return;
        }

        log.info("Добавление сценария: {}", payload);

        Scenario scenario = Scenario.builder()
                .hubId(hubEventAvro.getHubId())
                .name(payload.getName())
                .conditions(toConditions(payload.getConditions(), hubEventAvro.getHubId()))
                .actions(toActions(payload.getActions(), hubEventAvro.getHubId()))
                .build();
        scenarioRepository.save(scenario);
        log.info("Сценарий '{}' успешно сохранен", payload.getName());
    }

    @Override
    public String getType() {
        return ScenarioAddedEventAvro.class.getName();
    }

    private List<Condition> toConditions(List<ScenarioConditionAvro> conditions, String hubId) {
        validateSensorsExist(conditions.stream().map(ScenarioConditionAvro::getSensorId).toList(), hubId);

        return conditions.stream().map(condition ->
        {
            Sensor sensor = sensorRepository.findById(condition.getSensorId())
                    .orElseThrow(() -> new IllegalArgumentException("Sensor not found for id: " + condition.getSensorId()));

            return Condition.builder()
                    .sensor(sensor)
                    .type(condition.getType())
                    .operation(condition.getOperation())
                    .value(toValue(condition.getValue()))
                    .build();
        }).toList();
    }

    private List<Action> toActions(List<DeviceActionAvro> actions, String hubId) {
        validateSensorsExist(actions.stream().map(DeviceActionAvro::getSensorId).toList(), hubId);

        return actions.stream().map(action ->
        {
            Sensor sensor = sensorRepository.findById(action.getSensorId())
                    .orElseThrow(() -> new IllegalArgumentException("Sensor not found for id: " + action.getSensorId()));

            return Action.builder()
                    .sensor(sensor)
                    .type(action.getType())
                    .value(action.getValue() == null ? 0 : action.getValue())
                    .build();
        }).toList();
    }

    private void validateSensorsExist(List<String> sensorIds, String hubId) {
        if (!sensorRepository.existsByIdInAndHubId(sensorIds, hubId)) {
            log.error("Некоторые сенсоры не найдены для hubId={} и ids={}", hubId, sensorIds);
            throw new NoSuchElementException("Некоторые элементы не найдены");
        }
    }

    private Integer toValue(Object value) {
        if (value instanceof Integer intValue) {
            return intValue;
        } else if (value instanceof Boolean boolValue) {
            return boolValue ? 1 : 0;
        }
        return 0;
    }
}
