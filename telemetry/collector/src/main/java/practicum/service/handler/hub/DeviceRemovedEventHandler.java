package practicum.service.handler.hub;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import practicum.service.EventService;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

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

}
