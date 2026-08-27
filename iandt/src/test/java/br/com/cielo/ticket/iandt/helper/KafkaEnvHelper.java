package br.com.cielo.ticket.iandt.helper;

import lombok.NoArgsConstructor;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class KafkaEnvHelper {

    private static final String KAFKA_BOOTSTRAP_SERVERS = System.getProperty("kafka.bootstrap-servers", "localhost:9092");

    public static void sendPaymentMessage(UUID reservationId) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(props)) {
            String jsonMessage = """
                    {
                        "externalId": "%s"
                    }
                    """.formatted(reservationId);
            producer.send(new ProducerRecord<>("api-ticket_notification-creation_topic", reservationId.toString(), jsonMessage));
        }
    }
}
