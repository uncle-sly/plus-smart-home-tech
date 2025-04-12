package practicum.service.handler.sensor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import practicum.model.Action;
import practicum.model.Condition;
import practicum.model.Scenario;
import practicum.repository.ScenarioRepository;
import practicum.service.HubActionProducer;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class SnapshotsEventHandlerImpl implements SnapshotsEventHandler {

    private final ScenarioRepository scenarioRepository;
    private final HubActionProducer hubActionProducer;

    public void handle(SensorsSnapshotAvro snapshot) {

        String hubId = snapshot.getHubId();
        log.info("Обрабатываем снапшот от хаба: {}", hubId);

        if (snapshot.getSensorsState() == null) {
            log.warn("Состояние сенсоров отсутствует в снапшоте от хаба: {}", hubId);
            return;
        }

        scenarioRepository.findByHubId(hubId).stream()
                .filter(scenario -> isReady(scenario, snapshot))
                .forEach(scenario -> runActions(hubId, scenario.getName(), toActionMap(scenario.getActions())));
    }


    private boolean isReady(Scenario scenario, SensorsSnapshotAvro snapshot) {
        log.info("Условия: {}", scenario.getConditions());

        return scenario.getConditions().stream()
                .allMatch(condition -> checkCondition(condition.getSensor().getId(), condition, snapshot));
    }

    private Map<String, Action> toActionMap(List<Action> actions) {
        return actions.stream().collect(Collectors.toMap(
                action -> action.getSensor().getId(),
                action -> action
        ));
    }

    private boolean checkCondition(String sensorId, Condition condition, SensorsSnapshotAvro snapshot) {
        SensorStateAvro sensorState = snapshot.getSensorsState().get(sensorId);
        if (sensorState == null || sensorState.getData() == null) {
            log.warn("Нет данных для sensorId {} в снапшоте", sensorId);
            return false;
        }

        Object data = sensorState.getData();
        int currentValue = switch (condition.getType()) {
            case MOTION -> ((MotionSensorAvro) data).getMotion() ? 1 : 0;
            case LUMINOSITY -> ((LightSensorAvro) data).getLuminosity();
            case SWITCH -> ((SwitchSensorAvro) data).getState() ? 1 : 0;
            case TEMPERATURE -> ((ClimateSensorAvro) data).getTemperatureC();
            case CO2LEVEL -> ((ClimateSensorAvro) data).getCo2Level();
            case HUMIDITY -> ((ClimateSensorAvro) data).getHumidity();
        };
        return calculateCondition(currentValue, condition.getValue(), condition.getOperation());
    }

    private boolean calculateCondition(int currentValue, int targetValue, ConditionOperationAvro operation) {
        return switch (operation) {
            case EQUALS -> currentValue == targetValue;
            case GREATER_THAN -> currentValue > targetValue;
            case LOWER_THAN -> currentValue < targetValue;
        };
    }

    private void runActions(String hubId, String scenarioName, Map<String, Action> actions) {
        actions.forEach((sensorId, action) -> {
            hubActionProducer.sendAction(hubId, scenarioName, sensorId, action);
            log.info("Отправлено действие: хаб={}, сценарий={}, сенсор={}, действие={}",
                    hubId, scenarioName, sensorId, action);
        });
    }
}