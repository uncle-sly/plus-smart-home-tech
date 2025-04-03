package practicum.service.handler.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import practicum.repository.SensorRepository;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeviceRemovedEventHandler implements HubEventHandler{

    private final SensorRepository sensorRepository;

    @Override
    public void handle(HubEventAvro hubEventAvro) {

        if (!(hubEventAvro.getPayload() instanceof DeviceRemovedEventAvro payload)) {
            log.warn("Получено событие неизвестного типа: {}", hubEventAvro.getPayload().getClass().getName());
            return;
        }
//        DeviceRemovedEventAvro payload = (DeviceRemovedEventAvro) hubEventAvro.getPayload();
//        log.info("Устройство удалено: {}", payload);
//        sensorRepository.findByIdAndHubId(payload.getId(), hubEventAvro.getHubId())
//                .ifPresent(sensorRepository::delete);

        sensorRepository.findByIdAndHubId(payload.getId(), hubEventAvro.getHubId())
                .ifPresentOrElse(sensor -> {
                    sensorRepository.delete(sensor);
                    log.info("Удалено устройство: {}", payload);
                }, () -> log.warn("Устройство с id={} в hubId={} не найдено", payload.getId(), hubEventAvro.getHubId()));
    }

    @Override
    public String getType() {
        return DeviceRemovedEventAvro.class.getName();
    }

}