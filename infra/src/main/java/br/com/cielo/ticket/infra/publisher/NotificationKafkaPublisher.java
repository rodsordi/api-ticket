package br.com.cielo.ticket.infra.publisher;

import br.com.cielo.ticket.infra.evt.NotificationEvt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationKafkaPublisher {

    private final KafkaTemplate<String, NotificationEvt> kafkaTemplate;

    @Value("${message.notification-creation.topic:api-ticket_notification-creation_topic}")
    private String topicName;

    public void sendNotification(NotificationEvt notificationEvt) {
        if (notificationEvt == null) {
            return;
        }
        log.info("Publishing notification event to topic {}: {}", topicName, notificationEvt.getExternalId());
        kafkaTemplate.send(topicName, notificationEvt.getExternalId() != null ? notificationEvt.getExternalId().toString() : null, notificationEvt);
    }
}
