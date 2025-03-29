package practicum.service.handler.sensor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import practicum.service.EventService;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SwitchSensorProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;

import java.time.Instant;

import static practicum.mapper.SensorEventMapper.toSensorEventAvro;

@Component
@RequiredArgsConstructor
public class SwitchSensorEventHandler implements SensorEventHandler {

    private final EventService eventService;

    @Override
    public void handle(SensorEventProto sensorEvent) {
        eventService.collectSensorEvent(toSensorEventAvro(sensorEvent));
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.SWITCH_SENSOR_EVENT;
    }

//    private SensorEventAvro toSensorEventAvro(SensorEventProto sensorEvent) {
//        SwitchSensorProto switchProto = sensorEvent.getSwitchSensorEvent();
//        SwitchSensorAvro switchAvro = SwitchSensorAvro.newBuilder()
//                .setState(switchProto.getState())
//                .build();
//
//        return SensorEventAvro.newBuilder()
//                .setId(sensorEvent.getId())
//                .setHubId(sensorEvent.getHubId())
//                .setTimestamp(Instant.ofEpochSecond(sensorEvent.getTimestamp().getSeconds(), sensorEvent.getTimestamp().getNanos()))
//                .setPayload(switchAvro)
//                .build();
//    }

}
