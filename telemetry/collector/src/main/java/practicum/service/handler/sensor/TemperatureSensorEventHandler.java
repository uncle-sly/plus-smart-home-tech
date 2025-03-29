package practicum.service.handler.sensor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import practicum.service.EventService;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.TemperatureSensorProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

import java.time.Instant;

import static practicum.mapper.SensorEventMapper.toSensorEventAvro;

@Component
@RequiredArgsConstructor
public class TemperatureSensorEventHandler implements SensorEventHandler {


    private final EventService eventService;

    @Override
    public void handle(SensorEventProto sensorEvent) {
        eventService.collectSensorEvent(toSensorEventAvro(sensorEvent));
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.TEMPERATURE_SENSOR_EVENT;
    }

//    private SensorEventAvro toSensorEventAvro(SensorEventProto sensorEvent) {
//        TemperatureSensorProto temperatureProto = sensorEvent.getTemperatureSensorEvent();
//        TemperatureSensorAvro temperatureAvro = TemperatureSensorAvro.newBuilder()
//                .setTemperatureC(temperatureProto.getTemperatureC())
//                .setTemperatureF(temperatureProto.getTemperatureF())
//                .build();
//
//        return SensorEventAvro.newBuilder()
//                .setId(sensorEvent.getId())
//                .setHubId(sensorEvent.getHubId())
//                .setTimestamp(Instant.ofEpochSecond(sensorEvent.getTimestamp().getSeconds(), sensorEvent.getTimestamp().getNanos()))
//                .setPayload(temperatureAvro)
//                .build();
//    }

}
