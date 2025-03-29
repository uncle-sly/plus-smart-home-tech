package practicum.service.handler.sensor;

import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.C;
import org.springframework.stereotype.Component;
import practicum.service.EventService;
import ru.yandex.practicum.grpc.telemetry.event.MotionSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.time.Instant;

import static practicum.mapper.SensorEventMapper.toSensorEventAvro;

@Component
@RequiredArgsConstructor
public class MotionSensorEventHandler implements SensorEventHandler {

    private final EventService eventService;

    @Override
    public void handle(SensorEventProto sensorEvent) {
        eventService.collectSensorEvent(toSensorEventAvro(sensorEvent));
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.MOTION_SENSOR_EVENT;
    }

//    private SensorEventAvro toSensorEventAvro(SensorEventProto sensorEvent) {
//        MotionSensorProto motionProto = sensorEvent.getMotionSensorEvent();
//        MotionSensorAvro motionAvro = MotionSensorAvro.newBuilder()
//                .setLinkQuality(motionProto.getLinkQuality())
//                .setMotion(motionProto.getMotion())
//                .setVoltage(motionProto.getVoltage())
//                .build();
//
//        return SensorEventAvro.newBuilder()
//                .setId(sensorEvent.getId())
//                .setHubId(sensorEvent.getHubId())
//                .setTimestamp(Instant.ofEpochSecond(sensorEvent.getTimestamp().getSeconds(), sensorEvent.getTimestamp().getNanos()))
//                .setPayload(motionAvro)
//                .build();
//    }

}
