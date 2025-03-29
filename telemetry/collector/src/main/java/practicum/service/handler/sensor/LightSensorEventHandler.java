package practicum.service.handler.sensor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import practicum.service.EventService;
import ru.yandex.practicum.grpc.telemetry.event.LightSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.time.Instant;

import static practicum.mapper.SensorEventMapper.toSensorEventAvro;

@Component
@RequiredArgsConstructor
public class LightSensorEventHandler implements SensorEventHandler {

    private final EventService eventService;

    @Override
    public void handle(SensorEventProto sensorEvent) {
        eventService.collectSensorEvent(toSensorEventAvro(sensorEvent));
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR_EVENT;
    }

//    private SensorEventAvro toSensorEventAvro(SensorEventProto sensorEvent) {
//        LightSensorProto lightProto = sensorEvent.getLightSensorEvent();
//        LightSensorAvro lightAvro = LightSensorAvro.newBuilder()
//                .setLinkQuality(lightProto.getLinkQuality())
//                .setLuminosity(lightProto.getLuminosity())
//                .build();
//
//        return SensorEventAvro.newBuilder()
//                .setId(sensorEvent.getId())
//                .setHubId(sensorEvent.getHubId())
//                .setTimestamp(Instant.ofEpochSecond(sensorEvent.getTimestamp().getSeconds(), sensorEvent.getTimestamp().getNanos()))
//                .setPayload(lightAvro)
//                .build();
//    }

}
