package practicum.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import practicum.model.sensor.*;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Instant;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SensorEventMapper {

    public static SensorEventAvro toSensorEventAvro(SensorEventProto sensorEvent) {
        Object payload = switch (sensorEvent.getPayloadCase()) {
            case CLIMATE_SENSOR_EVENT -> ClimateSensorAvro.newBuilder()
                    .setTemperatureC(sensorEvent.getClimateSensorEvent().getTemperatureC())
                    .setHumidity(sensorEvent.getClimateSensorEvent().getHumidity())
                    .setCo2Level(sensorEvent.getClimateSensorEvent().getCo2Level())
                    .build();
            case LIGHT_SENSOR_EVENT -> LightSensorAvro.newBuilder()
                    .setLinkQuality(sensorEvent.getLightSensorEvent().getLinkQuality())
                    .setLuminosity(sensorEvent.getLightSensorEvent().getLuminosity())
                    .build();
            case MOTION_SENSOR_EVENT -> MotionSensorAvro.newBuilder()
                    .setLinkQuality(sensorEvent.getMotionSensorEvent().getLinkQuality())
                    .setMotion(sensorEvent.getMotionSensorEvent().getMotion())
                    .setVoltage(sensorEvent.getMotionSensorEvent().getVoltage())
                    .build();
            case SWITCH_SENSOR_EVENT -> SwitchSensorAvro.newBuilder()
                    .setState(sensorEvent.getSwitchSensorEvent().getState())
                    .build();
            case TEMPERATURE_SENSOR_EVENT -> TemperatureSensorAvro.newBuilder()
                    .setTemperatureC(sensorEvent.getTemperatureSensorEvent().getTemperatureC())
                    .setTemperatureF(sensorEvent.getTemperatureSensorEvent().getTemperatureF())
                    .build();
            default -> throw new IllegalArgumentException("Unsupported SensorEvent type: " + sensorEvent.getPayloadCase());
        };

        return SensorEventAvro.newBuilder()
                .setId(sensorEvent.getId())
                .setHubId(sensorEvent.getHubId())
                .setTimestamp(Instant.ofEpochSecond(sensorEvent.getTimestamp().getSeconds(), sensorEvent.getTimestamp().getNanos()))
                .setPayload(payload)
                .build();
    }
}