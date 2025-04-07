package practicum.service.handler.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import practicum.repository.ScenarioRepository;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;


@Slf4j
@RequiredArgsConstructor
@Component
public class ScenarioRemovedEventHandler implements HubEventHandler {

    private final ScenarioRepository scenarioRepository;

    @Override
    public void handle(HubEventAvro hubEventAvro) {

        if (!(hubEventAvro.getPayload() instanceof ScenarioRemovedEventAvro payload)) {
            log.warn("Получено событие неизвестного типа: {}", hubEventAvro.getPayload().getClass().getName());
            return;
        }
        log.info("Сценарий удален: {}", payload);

        scenarioRepository.findByHubIdAndName(hubEventAvro.getHubId(), payload.getName())
                .ifPresentOrElse(scenario -> {
                    scenarioRepository.delete(scenario);
                    log.info("Сценарий '{}' удален", payload.getName());
                }, () -> log.warn("Сценарий '{}' не найден в hubId={}", payload.getName(), hubEventAvro.getHubId()));
    }

    @Override
    public String getType() {
        return ScenarioRemovedEventAvro.class.getName();
    }
}