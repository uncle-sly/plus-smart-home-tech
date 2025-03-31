package ru.yandex.practicum.aggregator;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.KafkaClient;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

/**
 * Класс AggregationStarter, ответственный за запуск агрегации данных.
 */
@Slf4j
@Component
public class AggregationStarter {

    private final KafkaClient kafkaClient;
    private final AggregatorServiceImpl aggregatorService;
    private final String sensorTopic;
    private final String snapshotTopic;

    public AggregationStarter(KafkaClient kafkaClient, AggregatorServiceImpl aggregatorService,
                              @Value("${kafka.topic.sensor}") String sensorTopic,
                              @Value("${kafka.topic.snapshot}") String snapshotTopic) {
        this.kafkaClient = kafkaClient;
        this.aggregatorService = aggregatorService;
        this.sensorTopic = sensorTopic;
        this.snapshotTopic = snapshotTopic;
    }

    /**
     * Метод для начала процесса агрегации данных.
     * Подписывается на топики для получения событий от датчиков,
     * формирует снимок их состояния и записывает в кафку.
     */

    public void start() {
        Consumer<String, SensorEventAvro> consumer = kafkaClient.getConsumer();
        Producer<String, SpecificRecordBase> producer = kafkaClient.getProducer();

        try {
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
            consumer.subscribe(List.of(sensorTopic));

            while (true) {
                ConsumerRecords<String, SensorEventAvro> consumerRecords = consumer.poll(Duration.ofMillis(100));
                log.info("Начинаем обработку {} событий из Kafka", consumerRecords.count());

                for (ConsumerRecord<String, SensorEventAvro> consumerRecord : consumerRecords) {
                    Optional<SensorsSnapshotAvro> snapshot = aggregatorService.updateState(consumerRecord.value());
                    snapshot.ifPresent(snap -> {
                        ProducerRecord<String, SpecificRecordBase> producerRecord = new ProducerRecord<>(
                                snapshotTopic,
                                null,
                                snap.getTimestamp().toEpochMilli(),
                                snap.getHubId(),
                                snap
                        );
                        producer.send(producerRecord);
                        log.info("Отправили снимок состояния в Kafka для hubId={}", snap.getHubId());
                    });
                }
                consumer.commitSync();
            }

        } catch (WakeupException ignored) {
            // Ожидаемое исключение при выключении
            // игнорируем - закрываем консьюмер и продюсер в блоке finally
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {
//                 Перед тем, как закрыть продюсер и консьюмер, нужно убедится,
//                 что все сообщения, лежащие в буффере, отправлены и
//                 все оффсеты обработанных сообщений зафиксированы
//                 здесь нужно вызвать метод продюсера для сброса данных в буффере
//                 здесь нужно вызвать метод консьюмера для фиксиции смещений
            try {
                producer.flush();
                log.info("Выполнена команда flush");
                consumer.commitSync();
                log.info("Зафиксированы оффсеты");
            } catch (Exception e) {
                log.error("Ошибка при финальной отправке сообщений или фиксации оффсетов", e);
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
                log.info("Закрываем продюсер");
                producer.close();
            }
        }
    }
}
