package practicum.service.handler.hub;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import practicum.model.Action;
import practicum.model.Condition;
import practicum.model.Scenario;
import practicum.repository.ScenarioRepository;
import practicum.repository.SensorRepository;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class ScenarioAddedEventHandler implements HubEventHandler {

    private final ScenarioRepository scenarioRepository;
    private final SensorRepository sensorRepository;

    @Override
    public void handle(HubEventAvro hubEventAvro) {
//        ScenarioAddedEventAvro payload = (ScenarioAddedEventAvro) hubEventAvro.getPayload();

        if (!(hubEventAvro.getPayload() instanceof ScenarioAddedEventAvro payload)) {
            log.warn("Получено событие неизвестного типа: {}", hubEventAvro.getPayload().getClass().getName());
            return;
        }

        log.info("Добавлен сценарий: {}", payload);

        if (scenarioRepository.findByHubIdAndName(hubEventAvro.getHubId(), payload.getName()).isEmpty()) {
            Scenario scenario = Scenario.builder()
                    .hubId(hubEventAvro.getHubId())
                    .name(payload.getName())
                    .conditions(toConditions(payload.getConditions(), hubEventAvro.getHubId()))
                    .actions(toActions(payload.getActions(), hubEventAvro.getHubId()))
                    .build();
            scenarioRepository.save(scenario);
            log.info("Сценарий '{}' успешно сохранен", payload.getName());
        }
    }

    @Override
    public String getType() {
        return ScenarioAddedEventAvro.class.getName();
    }

/*    private Map<String, Condition> toConditions(List<ScenarioConditionAvro> conditions, String hubId) {
        validateSensorsExist(conditions.stream().map(ScenarioConditionAvro::getSensorId).toList(), hubId);

        return conditions.stream().collect(Collectors.toMap(
                ScenarioConditionAvro::getSensorId,
                condition -> Condition.builder()
                        .type(condition.getType())
                        .operation(condition.getOperation())
                        .value(toValue(condition.getValue()))
                        .build()
        ));
    }*/
private List<Condition> toConditions(List<ScenarioConditionAvro> conditions, String hubId) {
    validateSensorsExist(conditions.stream().map(ScenarioConditionAvro::getSensorId).toList(), hubId);

    return conditions.stream().map(condition ->
            Condition.builder()
                    .type(condition.getType())
                    .operation(condition.getOperation())
                    .value(toValue(condition.getValue()))
                    .build()
    ).toList();
}



/*
    private Map<String, Action> toActions(List<DeviceActionAvro> actions, String hubId) {
        validateSensorsExist(actions.stream().map(DeviceActionAvro::getSensorId).toList(), hubId);

        return actions.stream().collect(Collectors.toMap(
                DeviceActionAvro::getSensorId,
                action -> Action.builder()
                        .type(action.getType())
                        .value(action.getValue() == null ? 0 : action.getValue())
                        .build()
        ));
    }
*/
private List<Action> toActions(List<DeviceActionAvro> actions, String hubId) {
    validateSensorsExist(actions.stream().map(DeviceActionAvro::getSensorId).toList(), hubId);

    return actions.stream().map(action ->
            Action.builder()
                    .type(action.getType())
                    .value(action.getValue() == null ? 0 : action.getValue())
                    .build()
    ).toList();
}



    private void validateSensorsExist(List<String> sensorIds, String hubId) {
        if (!sensorRepository.existsByIdInAndHubId(sensorIds, hubId)) {
            log.error("Некоторые сенсоры не найдены для hubId={} и ids={}", hubId, sensorIds);
            throw new NoSuchElementException("Некоторые элементы не найдены");
        }
    }

//    private Map<String, Condition> toConditions(List<ScenarioConditionAvro> conditions, String hubId) {
//        List<String> ids = conditions.stream()
//                .map(ScenarioConditionAvro::getSensorId)
//                .toList();
//        if (sensorRepository.existsByIdInAndHubId(ids, hubId)) {
//            return conditions.stream()
//                    .collect(Collectors.toMap(
//                            ScenarioConditionAvro::getSensorId,
//                            condition -> Condition.builder()
//                                    .type(condition.getType())
//                                    .operation(condition.getOperation())
//                                    .value(toValue(condition.getValue()))
//                                    .build()
//                    ));
//        } else {
//            throw new NoSuchElementException("Элемент не найден");
//        }
//    }
//
//    private Map<String, Action> toActions(List<DeviceActionAvro> actions, String hubId) {
//        List<String> ids = actions.stream()
//                .map(DeviceActionAvro::getSensorId)
//                .toList();
//        if (sensorRepository.existsByIdInAndHubId(ids, hubId)) {
//            return actions.stream()
//                    .collect(Collectors.toMap(
//                            DeviceActionAvro::getSensorId,
//                            action -> Action.builder()
//                                    .type(action.getType())
//                                    .value(action.getValue() == null ? 0 : action.getValue())
//                                    .build()
//                    ));
//        } else {
//            throw new NoSuchElementException("Элемент не найден");
//        }
//    }

//    private Integer toValue(Object value) {
//        if (value instanceof Integer) {
//            return (Integer) value;
//        } else if (value instanceof Boolean) {
//            if ((Boolean) value) {
//                return 1;
//            }
//        }
//        return 0;
//    }

    private Integer toValue(Object value) {
        if (value instanceof Integer intValue) {
            return intValue;
        } else if (value instanceof Boolean boolValue) {
            return boolValue ? 1 : 0;
        }
        return 0;
    }



}
