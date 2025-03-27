package practicum.kafka;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.stereotype.Component;
import practicum.serialization.GeneralAvroSerializer;

import java.time.Duration;
import java.util.Properties;

@Component
public class KafkaProducerImpl implements KafkaProducer {

    private final String bootstrapServers;
    private Producer<String, SpecificRecordBase> producer;


    public KafkaProducerImpl(@Value("${kafka.bootstrap.server}") String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    @Override
    public Producer<String, SpecificRecordBase> getProducer() {
        if (producer == null) {
            initProducer();
        }
        return producer;
    }

    @Override
    public ProducerRecord<String, SpecificRecordBase> getProducerRecord(String topic, SpecificRecordBase record) {
        return new ProducerRecord<>(topic, record);
    }

    private void initProducer() {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GeneralAvroSerializer.class);

        producer = new org.apache.kafka.clients.producer.KafkaProducer<>(config);
    }


    @Override
    public void close() {
        if (producer != null) {
            producer.flush();
            producer.close(Duration.ofMillis(100));
        }
    }
}
