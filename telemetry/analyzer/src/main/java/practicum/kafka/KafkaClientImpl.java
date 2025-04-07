package practicum.kafka;

import deserialization.HubEventDeserializer;
import deserialization.SensorsSnapshotDeserializer;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.Properties;


@Component
public class KafkaClientImpl implements KafkaClient {

    private final String bootstrapServers;
    private final String snapshotGroupId;
    private final String hubGroupId;

    private Consumer<String, SensorsSnapshotAvro> sensorsSnapshotConsumer;
    private Consumer<String, HubEventAvro> hubEventConsumer;


    public KafkaClientImpl(@Value("${kafka.bootstrap.server}") String bootstrapServers,
                           @Value("${kafka.consumer.snapshot.group.id}") String snapshotGroupId,
                           @Value("${kafka.consumer.hub.group.id}") String hubGroupId) {
        this.bootstrapServers = bootstrapServers;
        this.snapshotGroupId = snapshotGroupId;
        this.hubGroupId = hubGroupId;
    }

    @Override
    public Consumer<String, SensorsSnapshotAvro> getSensorsSnapshotConsumer() {
        if (sensorsSnapshotConsumer == null) {
            initSnapshotConsumer();
        }
        return sensorsSnapshotConsumer;
    }

    private void initSnapshotConsumer() {
        Properties config = new Properties();
        config.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, snapshotGroupId);
        config.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SensorsSnapshotDeserializer.class);
        sensorsSnapshotConsumer = new KafkaConsumer<>(config);
    }


    @Override
    public Consumer<String, HubEventAvro> getHubEventConsumer() {
        if (hubEventConsumer == null) {
            initHubConsumer();
        }
        return hubEventConsumer;
    }

    private void initHubConsumer() {
        Properties config = new Properties();
        config.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, hubGroupId);
        config.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, HubEventDeserializer.class);
        hubEventConsumer = new KafkaConsumer<>(config);
    }

    @Override
    public void close() {
    }
}
