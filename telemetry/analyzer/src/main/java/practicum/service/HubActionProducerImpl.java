package practicum.service;

import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import practicum.model.Action;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc.HubRouterControllerBlockingStub;

import java.time.Instant;


@Slf4j
@Service
public class HubActionProducerImpl implements HubActionProducer {

    private final HubRouterControllerBlockingStub hubRouterClient;

    public HubActionProducerImpl(@GrpcClient("hub-router") HubRouterControllerBlockingStub hubRouterClient) {
        this.hubRouterClient = hubRouterClient;
    }

    public void sendAction(String hubId, String scenarioName, String sensorId, Action action) {

        if (hubId == null || scenarioName == null || sensorId == null || action == null) {
            log.warn("Один из аргументов sendAction() null: hubId={}, scenarioName={}, sensorId={}, action={}",
                    hubId, scenarioName, sensorId, action);
            return;
        }

        Instant now = Instant.now();
        DeviceActionRequest deviceActionRequest = buildDeviceActionRequest(hubId, scenarioName, sensorId, action, now);

        log.info("Отправка действия в Hub Router: hubId={}, scenarioName={}, sensorId={}, action={}",
                hubId, scenarioName, sensorId, action.getType().name());

        try {
            hubRouterClient.handleDeviceAction(deviceActionRequest);
        } catch (Exception e) {
            log.error("Ошибка при отправке действия в Hub Router: {}", e.getMessage(), e);
        }
    }

    private DeviceActionRequest buildDeviceActionRequest(String hubId, String scenarioName, String sensorId,
                                                         Action action, Instant timestamp) {
        return DeviceActionRequest.newBuilder()
                .setHubId(hubId)
                .setScenarioName(scenarioName)
                .setAction(DeviceActionProto.newBuilder()
                        .setSensorId(sensorId)
                        .setType(mapActionType(action))
                        .setValue(action.getValue())
                        .build())
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(timestamp.getEpochSecond())
                        .setNanos(timestamp.getNano())
                        .build())
                .build();
    }

    private ActionTypeProto mapActionType(Action action) {
        try {
            return ActionTypeProto.valueOf(action.getType().name());
        } catch (IllegalArgumentException e) {
            log.error("Неизвестный тип действия: {}", action.getType(), e);
            return ActionTypeProto.UNRECOGNIZED;
        }
    }
}