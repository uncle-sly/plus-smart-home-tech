package practicum.service.handler.sensor;

import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

public interface SnapshotsEventHandler {

    void handle(SensorsSnapshotAvro snapshot);

}
