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

/*    public void handle(SensorsSnapshotAvro snapshot) {

        String hubId = snapshot.getHubId();
        log.info("Обрабатываем снапшот от хаба: {}", hubId);

        if (snapshot.getSensorsState() == null) {
            log.warn("Состояние сенсоров отсутствует в снапшоте от хаба: {}", hubId);
            return;
        }

        scenarioRepository.findByHubId(hubId).stream()
                .filter(scenario -> isReady(scenario, snapshot))
                .forEach(scenario -> runActions(hubId, scenario.getName(), scenario.getActions()));
    }*/

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




/*    private boolean isReady(Scenario scenario, SensorsSnapshotAvro snapshot) {
        log.info("Условия: {}", scenario.getConditions());

        return scenario.getConditions().entrySet().stream()
                .allMatch(entry -> checkCondition(entry.getKey(), entry.getValue(), snapshot));

//        for (String sensorId : scenario.getConditions().keySet()) {
//            if (!checkCondition(sensorId, scenario.getConditions().get(sensorId), snapshot)) {
//                return false;
//            }
//        }
//        return true;
    }*/
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

//        if (sensorState == null) {
//            log.warn("Нет данных для sensorId {} в снапшоте", sensorId);
//            return false;
//        }

//        return switch (condition.getType()) {
//            case MOTION -> calculateCondition(((MotionSensorAvro) sensorState.getData()).getMotion() ? 1 : 0,
//                    condition.getValue(), condition.getOperation());
//            case LUMINOSITY -> calculateCondition(((LightSensorAvro) sensorState.getData()).getLuminosity(),
//                    condition.getValue(), condition.getOperation());
//            case SWITCH -> calculateCondition(((SwitchSensorAvro) sensorState.getData()).getState() ? 1 : 0,
//                    condition.getValue(), condition.getOperation());
//            case TEMPERATURE -> calculateCondition(((ClimateSensorAvro) sensorState.getData()).getTemperatureC(),
//                    condition.getValue(), condition.getOperation());
//            case CO2LEVEL -> calculateCondition(((ClimateSensorAvro) sensorState.getData()).getCo2Level(),
//                    condition.getValue(), condition.getOperation());
//            case HUMIDITY -> calculateCondition(((ClimateSensorAvro) sensorState.getData()).getHumidity(),
//                    condition.getValue(), condition.getOperation());
//        };
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

    //    private void runActions(String hubId, String scenarioName, Map<String, Action> actions) {
    //        actions.keySet()
    //                .forEach(sensorId -> hubActionProducer.sendAction(hubId, scenarioName, sensorId, actions.get(sensorId)));
    //    }

    private void runActions(String hubId, String scenarioName, Map<String, Action> actions) {
        actions.forEach((sensorId, action) -> {
            hubActionProducer.sendAction(hubId, scenarioName, sensorId, action);
            log.info("Отправлено действие: хаб={}, сценарий={}, сенсор={}, действие={}",
                    hubId, scenarioName, sensorId, action);
        });
    }

}
