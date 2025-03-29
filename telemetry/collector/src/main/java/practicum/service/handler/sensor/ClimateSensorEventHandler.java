package practicum.service.handler.sensor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import practicum.service.EventService;
import ru.yandex.practicum.grpc.telemetry.event.ClimateSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.time.Instant;

import static practicum.mapper.SensorEventMapper.toSensorEventAvro;

@Component
@RequiredArgsConstructor
public class ClimateSensorEventHandler implements SensorEventHandler {

    private final EventService eventService;

    @Override
    public void handle(SensorEventProto sensorEvent) {
        eventService.collectSensorEvent(toSensorEventAvro(sensorEvent));
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.CLIMATE_SENSOR_EVENT;
    }

//    private SensorEventAvro toSensorEventAvro(SensorEventProto sensorEvent) {
//        ClimateSensorProto climateProto = sensorEvent.getClimateSensorEvent();
//        ClimateSensorAvro climateAvro = ClimateSensorAvro.newBuilder()
//                .setTemperatureC(climateProto.getTemperatureC())
//                .setHumidity(climateProto.getHumidity())
//                .setCo2Level(climateProto.getCo2Level())
//                .build();
//
//        return SensorEventAvro.newBuilder()
//                .setId(sensorEvent.getId())
//                .setHubId(sensorEvent.getHubId())
//                .setTimestamp(Instant.ofEpochSecond(sensorEvent.getTimestamp().getSeconds(), sensorEvent.getTimestamp().getNanos()))
//                .setPayload(climateAvro)
//                .build();
//    }

}
