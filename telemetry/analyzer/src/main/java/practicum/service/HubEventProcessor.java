package practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import practicum.kafka.KafkaClient;
import practicum.service.handler.hub.HubEventHandler;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class HubEventProcessor implements Runnable {

        private static final Duration POLL_TIMEOUT = Duration.ofMillis(500);

        private final String hubTopic;
        private final KafkaClient kafkaClient;
        private final Map<String, HubEventHandler> handlerMap;

//        public HubEventProcessor(KafkaClient kafkaClient, Set<HubEventHandler> hubEventHandlers,
//                                 @Value("${kafka.topic.hub}") String hubTopic) {
//                this.hubTopic = hubTopic;
//                this.kafkaClient = kafkaClient;
//                this.handlerMap = hubEventHandlers.stream()
//                        .collect(Collectors.toMap(
//                                HubEventHandler::getType,
//                                Function.identity()
//                        ));
//        }
        public HubEventProcessor(KafkaClient kafkaClient, Set<HubEventHandler> hubEventHandlers,
                                 @Value("${kafka.topic.hub}") String hubTopic) {
                this.hubTopic = hubTopic;
                this.kafkaClient = kafkaClient;
                this.handlerMap = hubEventHandlers.stream()
                        .collect(Collectors.toMap(HubEventHandler::getType, Function.identity()));
        }

        @Override
        public void run() {
                Consumer<String, HubEventAvro> consumer = kafkaClient.getHubEventConsumer();

                try {
                        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
                        consumer.subscribe(List.of(hubTopic));
                        log.info("Подписка на HUB топик {} ", hubTopic);

                        while (true) {
                                ConsumerRecords<String, HubEventAvro> records = consumer.poll(POLL_TIMEOUT);
                                for (ConsumerRecord<String, HubEventAvro> record : records) {
                                        log.info("topic = {}, partition = {}, offset = {}, record = {}",
                                                record.topic(), record.partition(), record.offset(), record.value());
                                        HubEventHandler handler = handlerMap.get(record.value().getPayload().getClass().getName());
                                        if (handler != null) {
                                                handler.handle(record.value());
                                                log.info("Hub событие обработано.");
                                        }
                                }
                          consumer.commitAsync();
                        }

                } catch (WakeupException ignored) {
                } catch (Exception ex) {
                        log.error("Ошибка чтения данных из топика {}", hubTopic);
                        log.error(ex.getMessage(), ex);
                } finally {
                        try {
                                consumer.commitSync();
                        } finally {
                                consumer.close(Duration.ofMillis(100));
                        }
                }
        }
}