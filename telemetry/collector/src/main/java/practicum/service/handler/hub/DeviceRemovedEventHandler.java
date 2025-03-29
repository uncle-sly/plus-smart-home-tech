package practicum.service.handler.hub;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import practicum.service.EventService;
import ru.yandex.practicum.grpc.telemetry.event.DeviceAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceRemovedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Instant;

import static practicum.mapper.HubEventMapper.toHubEventAvro;

@Component
@RequiredArgsConstructor
public class DeviceRemovedEventHandler implements HubEventHandler {

    private final EventService eventService;

    @Override
    public void handle(HubEventProto hubEvent) {
        eventService.collectHubEvent(toHubEventAvro(hubEvent));

    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_REMOVED;
    }

//    private HubEventAvro toHubEventAvro(HubEventProto hubEvent) {
//        DeviceRemovedEventProto deviceProto = hubEvent.getDeviceRemoved();
//        DeviceRemovedEventAvro deviceAvro = DeviceRemovedEventAvro.newBuilder()
//                .setId(deviceProto.getId())
//                .build();
//
//        return HubEventAvro.newBuilder()
//                .setHubId(hubEvent.getHubId())
//                .setTimestamp(Instant.ofEpochSecond(hubEvent.getTimestamp().getSeconds(), hubEvent.getTimestamp().getNanos()))
//                .setPayload(deviceAvro)
//                .build();
//    }

}
