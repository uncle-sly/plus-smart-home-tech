package practicum.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import practicum.model.sensor.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SensorEventMapper {

    public static SensorEventAvro toSensorEventAvro(SensorEvent sensorEvent) {

        Object payload;

        switch (sensorEvent) {
            case ClimateSensorEvent event -> payload = ClimateSensorAvro.newBuilder()
                    .setTemperatureC(event.getTemperature_c())
                    .setHumidity(event.getHumidity())
                    .setCo2Level(event.getCo2_level())
                    .build();

            case LightSensorEvent event -> payload = LightSensorAvro.newBuilder()
                    .setLinkQuality(event.getLinkQuality())
                    .setLuminosity(event.getLuminosity())
                    .build();

            case MotionSensorEvent event -> payload = MotionSensorAvro.newBuilder()
                    .setLinkQuality(event.getLink_quality())
                    .setMotion(event.isMotion())
                    .setVoltage(event.getVoltage())
                    .build();

            case SwitchSensorEvent event -> payload = SwitchSensorAvro.newBuilder()
                    .setState(event.isState())
                    .build();

            case TemperatureSensorEvent event -> payload = TemperatureSensorAvro.newBuilder()
                    .setTemperatureC(event.getTemperature_c())
                    .setTemperatureF(event.getTemperature_f())
                    .build();

            default -> throw new IllegalStateException("Unexpected Event Value: " + sensorEvent);
        }

        return SensorEventAvro.newBuilder()
                .setId(sensorEvent.getId())
                .setHubId(sensorEvent.getHubId())
                .setTimestamp(sensorEvent.getTimestamp())
                .setPayload(payload)
                .build();
    }

}
