package ru.yandex.practicum.aggregator;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Service
public class AggregatorServiceImpl implements AggregatorService {

    private final Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();

    @Override
    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        SensorsSnapshotAvro snapshot = snapshots.getOrDefault(event.getHubId(), SensorsSnapshotAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(Instant.now())
                .setSensorsState(new HashMap<>())
                .build()
        );
        SensorStateAvro oldState = snapshot.getSensorsState().get(event.getId());

        if (oldState != null && (oldState.getTimestamp().isAfter(event.getTimestamp()) || oldState.getData().equals(event.getPayload()))) {
            return Optional.empty();
        }
        SensorStateAvro newState = SensorStateAvro.newBuilder()
                .setTimestamp(event.getTimestamp())
                .setData(event.getPayload())
                .build();

        snapshot.getSensorsState().put(event.getId(), newState);
        snapshot.setTimestamp(newState.getTimestamp());
        snapshots.put(snapshot.getHubId(), snapshot);
        return Optional.of(snapshot);
    }

}
