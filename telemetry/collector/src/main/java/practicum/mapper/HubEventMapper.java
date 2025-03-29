package practicum.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Instant;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HubEventMapper {

    public static HubEventAvro toHubEventAvro(HubEventProto hubEvent) {
        Object payload = switch (hubEvent.getPayloadCase()) {
            case DEVICE_ADDED -> DeviceAddedEventAvro.newBuilder()
                    .setId(hubEvent.getDeviceAdded().getId())
                    .setType(DeviceTypeAvro.valueOf(hubEvent.getDeviceAdded().getType().name()))
                    .build();
            case DEVICE_REMOVED -> DeviceRemovedEventAvro.newBuilder()
                    .setId(hubEvent.getDeviceRemoved().getId())
                    .build();
            case SCENARIO_ADDED -> ScenarioAddedEventAvro.newBuilder()
                    .setName(hubEvent.getScenarioAdded().getName())
                    .setConditions(toScenarioConditionAvros(hubEvent.getScenarioAdded().getConditionList()))
                    .setActions(toDeviceActionAvros(hubEvent.getScenarioAdded().getActionList()))
                    .build();
            case SCENARIO_REMOVED -> ScenarioRemovedEventAvro.newBuilder()
                    .setName(hubEvent.getScenarioRemoved().getName())
                    .build();
            default -> throw new IllegalArgumentException("Unsupported HubEventProto type: " + hubEvent.getPayloadCase());
        };

        return HubEventAvro.newBuilder()
                .setHubId(hubEvent.getHubId())
                .setTimestamp(Instant.ofEpochSecond(hubEvent.getTimestamp().getSeconds(), hubEvent.getTimestamp().getNanos()))
                .setPayload(payload)
                .build();
    }

    public static List<DeviceActionAvro> toDeviceActionAvros(List<DeviceActionProto> actions) {
        return actions.stream()
                .map(action -> DeviceActionAvro.newBuilder()
                        .setSensorId(action.getSensorId())
                        .setType(ActionTypeAvro.valueOf(action.getType().name()))
                        .setValue(action.getValue())
                        .build())
                .toList();
    }

    public static List<ScenarioConditionAvro> toScenarioConditionAvros(List<ScenarioConditionProto> conditions) {
        return conditions.stream()
                .map(condition -> {
                    Object value = switch (condition.getValueCase()) {
                        case INT_VALUE -> condition.getIntValue();
                        case BOOL_VALUE -> condition.getBoolValue();
                        default -> null;
                    };

                    return ScenarioConditionAvro.newBuilder()
                            .setSensorId(condition.getSensorId())
                            .setValue(value)
                            .setOperation(ConditionOperationAvro.valueOf(condition.getOperation().name()))
                            .setType(ConditionTypeAvro.valueOf(condition.getType().name()))
                            .build();
                })
                .toList();
    }
}