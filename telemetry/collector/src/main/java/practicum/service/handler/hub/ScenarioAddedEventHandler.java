package practicum.service.handler.hub;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import practicum.service.EventService;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Instant;
import java.util.List;

import static practicum.mapper.HubEventMapper.toHubEventAvro;

@Component
@RequiredArgsConstructor
public class ScenarioAddedEventHandler implements HubEventHandler {

    private final EventService eventService;

    @Override
    public void handle(HubEventProto hubEvent) {
        eventService.collectHubEvent(toHubEventAvro(hubEvent));
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

//    private HubEventAvro toHubEventAvro(HubEventProto hubEvent) {
//        ScenarioAddedEventProto scenarioProto = hubEvent.getScenarioAdded();
//        ScenarioAddedEventAvro scenarioAvro = ScenarioAddedEventAvro.newBuilder()
//                .setName(scenarioProto.getName())
//                .setConditions(toScenarioConditionAvro(scenarioProto.getConditionList()))
//                .setActions(toDeviceActionAvro(scenarioProto.getActionList()))
//                .build();
//
//        return HubEventAvro.newBuilder()
//                .setHubId(hubEvent.getHubId())
//                .setTimestamp(Instant.ofEpochSecond(hubEvent.getTimestamp().getSeconds(), hubEvent.getTimestamp().getNanos()))
//                .setPayload(scenarioAvro)
//                .build();
//
//    }
//
//    private List<ScenarioConditionAvro> toScenarioConditionAvro(List<ScenarioConditionProto> conditions) {
//        return conditions.stream()
//                .map(condition -> {
//                    Object value = switch (condition.getValueCase()) {
//                        case INT_VALUE -> condition.getIntValue();
//                        case BOOL_VALUE -> condition.getBoolValue();
//                        default -> null;
//                    };
//
//                    return ScenarioConditionAvro.newBuilder()
//                            .setSensorId(condition.getSensorId())
//                            .setValue(value)
//                            .setOperation(ConditionOperationAvro.valueOf(condition.getOperation().name()))
//                            .setType(ConditionTypeAvro.valueOf(condition.getType().name()))
//                            .build();
//                })
//                .toList();
//    }
//
//    private List<DeviceActionAvro> toDeviceActionAvro(List<DeviceActionProto> actions) {
//        return actions.stream()
//                .map(action -> DeviceActionAvro.newBuilder()
//                        .setSensorId(action.getSensorId())
//                        .setType(ActionTypeAvro.valueOf(action.getType().name()))
//                        .setValue(action.getValue())
//                        .build())
//                .toList();
//    }

}
