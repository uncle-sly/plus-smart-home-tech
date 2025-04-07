package practicum.service.handler.hub;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import practicum.model.Sensor;
import practicum.repository.SensorRepository;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeviceAddedEventHandler implements HubEventHandler {

    private final SensorRepository sensorRepository;

    @Override
    @Transactional
    public void handle(HubEventAvro event) {
        DeviceAddedEventAvro payload = (DeviceAddedEventAvro) event.getPayload();

        log.info("Добавление датчика: {}", payload);
        if (!sensorRepository.existsByIdInAndHubId(List.of(payload.getId()), event.getHubId())) {
            Sensor sensor = Sensor.builder()
                    .id(payload.getId())
                    .hubId(event.getHubId())
                    .build();
            sensorRepository.save(sensor);
            log.info("Добавлен датчик: {}", sensor);
        }
    }


    @Override
    public String getType() {
        return DeviceAddedEventAvro.class.getName();
    }

}