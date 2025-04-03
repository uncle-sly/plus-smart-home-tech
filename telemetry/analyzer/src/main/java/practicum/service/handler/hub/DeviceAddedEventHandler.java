package practicum.service.handler.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import practicum.model.Sensor;
import practicum.repository.SensorRepository;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeviceAddedEventHandler implements HubEventHandler {

    private final SensorRepository sensorRepository;

    @Override
    public void handle(HubEventAvro hubEventAvro) {

        if (!(hubEventAvro.getPayload() instanceof DeviceAddedEventAvro payload)) {
            log.warn("Получено событие неподходящего типа: {}", hubEventAvro.getPayload().getClass().getName());
            return;
        }

        log.info("Устройство добавлено: {}", payload);
            Sensor sensor = Sensor.builder()
                    .id(payload.getId())
                    .hubId(hubEventAvro.getHubId())
                    .build();
            sensorRepository.save(sensor);
        log.info("Устройство добавлено и сохранено в БД: {}", sensor);
    }

    @Override
    public String getType() {
        return DeviceAddedEventAvro.getClassSchema().getName();
    }

}
