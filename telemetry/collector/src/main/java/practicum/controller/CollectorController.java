package practicum.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import practicum.model.hub.HubEvent;
import practicum.model.sensor.SensorEvent;
import practicum.service.EventService;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CollectorController {

    @Value("${kafka.topic.sensor}")
    private String SENSOR_TOPIC;
    @Value("${kafka.topic.hub}")
    private String HUB_TOPIC;

    private final EventService eventService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/sensors")
    public void collectSensorEvent(@Valid @RequestBody SensorEvent sensorEvent) {
        log.info("Collecting sensor event: {}", sensorEvent);
        eventService.collectSensorEvent(sensorEvent, SENSOR_TOPIC);
        log.info("Collected sensor event: {}", sensorEvent);

    }


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/hubs")
    public void collectHubEvent(@Valid @RequestBody HubEvent hubEvent) {
        log.info("Collecting hub event: {}", hubEvent);
        eventService.collectHubEvent(hubEvent, HUB_TOPIC);
        log.info("Collected hub event: {}", hubEvent);
    }

}
