package practicum.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import practicum.model.hub.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HubEventMapper {

    public static HubEventAvro toHubEventAvro(HubEvent hubEvent) {

        Object payload;

        switch (hubEvent) {

            case DeviceAddedEvent event -> payload = DeviceAddedEventAvro.newBuilder()
                    .setId(event.getId())
                    .setType(DeviceTypeAvro.valueOf(event.getDeviceType().name()))
                    .build();

            case DeviceRemovedEvent event -> payload = DeviceRemovedEventAvro.newBuilder()
                    .setId(event.getId())
                    .build();

            case ScenarioAddedEvent event -> payload = ScenarioAddedEventAvro.newBuilder()
                    .setName(event.getName())
                    .setActions(toDeviceActionAvros(event.getActions()))
                    .setConditions(toScenarioConditionAvros(event.getConditions()))
                    .build();

            case ScenarioRemovedEvent event -> payload = ScenarioRemovedEventAvro.newBuilder()
                    .setName(event.getName())
                    .build();

            default -> throw new IllegalStateException("Unexpected Hub Value: " + hubEvent);
        }

        return HubEventAvro.newBuilder()
                .setHubId(hubEvent.getHubId())
                .setTimestamp(hubEvent.getTimestamp())
                .setPayload(payload)
                .build();
    }

    public static List<DeviceActionAvro> toDeviceActionAvros(List<DeviceAction> actions) {
        return actions.stream()
                .map(action -> DeviceActionAvro.newBuilder()
                        .setSensorId(action.getSensorId())
                        .setType(ActionTypeAvro.valueOf(action.getType().name()))
                        .setValue(action.getValue())
                        .build())
                .toList();
    }

    public static List<ScenarioConditionAvro> toScenarioConditionAvros(List<ScenarioCondition> conditions) {
        return conditions.stream()
                .map(condition -> ScenarioConditionAvro.newBuilder()
                        .setSensorId(condition.getSensorId())
                        .setType(ConditionTypeAvro.valueOf(condition.getType().name()))
                        .setOperation(ConditionOperationAvro.valueOf(condition.getOperation().name()))
                        .setValue(condition.getValue())
                        .build())
                .toList();
    }

}
