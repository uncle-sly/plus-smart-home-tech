package practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import practicum.kafka.KafkaClient;
import practicum.service.handler.sensor.SnapshotsEventHandler;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.List;


@Slf4j
@Component
public class SnapshotProcessor {

    private static final Duration POLL_TIMEOUT = Duration.ofMillis(1000);

    private final String snapshotTopic;
    private final KafkaClient kafkaClient;
    private final SnapshotsEventHandler snapshotsEventHandler;

    public SnapshotProcessor(KafkaClient kafkaClient, SnapshotsEventHandler snapshotsEventHandler,
                             @Value("${kafka.topic.snapshot}") String snapshotTopic) {
        this.snapshotTopic = snapshotTopic;
        this.kafkaClient = kafkaClient;
        this.snapshotsEventHandler = snapshotsEventHandler;
    }

    public void start() {
        Consumer<String, SensorsSnapshotAvro> consumer = kafkaClient.getSensorsSnapshotConsumer();

        try {
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
            consumer.subscribe(List.of(snapshotTopic));

            while (true) {
                ConsumerRecords<String, SensorsSnapshotAvro> records = consumer.poll(POLL_TIMEOUT);
                for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                    log.info("topic = {}, partition = {}, offset = {}, record = {}",
                            record.topic(), record.partition(), record.offset(), record.value());
                    snapshotsEventHandler.handle(record.value());
                    log.info("Hub событие обработано.");
                }
                consumer.commitSync();
            }


        } catch (
                WakeupException ignored) {
        } catch (Exception ex) {
            log.error("Ошибка чтения данных из топика {}", snapshotTopic);
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