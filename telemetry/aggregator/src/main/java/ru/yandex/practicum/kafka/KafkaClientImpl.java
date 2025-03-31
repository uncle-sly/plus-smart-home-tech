package ru.yandex.practicum.kafka;

import deserialization.SensorEventDeserializer;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import practicum.serialization.GeneralAvroSerializer;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.time.Duration;
import java.util.Properties;

@Component
public class KafkaClientImpl implements KafkaClient {

    private final String bootstrapServers;
    private final String consumerGroupId;

    private Producer<String, SpecificRecordBase> producer;
    private Consumer<String, SensorEventAvro> consumer;

    public KafkaClientImpl(@Value("${kafka.bootstrap.server}") String bootstrapServers,
                           @Value("${kafka.group.id}") String consumerGroupId) {
        this.bootstrapServers = bootstrapServers;
        this.consumerGroupId = consumerGroupId;
    }

    @Override
    public Producer<String, SpecificRecordBase> getProducer() {
        if (producer == null) {
            initProducer();
        }
        return producer;
    }

//    @Override
//    public ProducerRecord<String, SpecificRecordBase> getProducerRecord(String topic, SpecificRecordBase record) {
//        return new ProducerRecord<>(topic, record);
//    }

    private void initProducer() {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GeneralAvroSerializer.class);

        producer = new org.apache.kafka.clients.producer.KafkaProducer<>(config);
    }

    @Override
    public Consumer<String, SensorEventAvro> getConsumer() {
        if (consumer == null) {
            initConsumer();
        }
        return consumer;
    }

    private void initConsumer() {
        Properties config = new Properties();
        config.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
        config.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SensorEventDeserializer.class);
        consumer = new KafkaConsumer<>(config);
    }


    @Override
    public void close() {
        if (producer != null) {
            producer.flush();
            producer.close(Duration.ofMillis(100));
        }
    }
}
