package br.com.cielo.ticket.application.adapter;

import br.com.cielo.ticket.application.v1.msg.EmailMsg;
import br.com.cielo.ticket.application.v1.msg.NotificationMsg;
import br.com.cielo.ticket.domain.port.ReservationEventPublisherPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationEventPublisherAdapter implements ReservationEventPublisherPort {

    private final KafkaTemplate<String, NotificationMsg> kafkaTemplate;

    @Value("${message.notification-creation.topic:api-ticket_notification-creation_topic}")
    private String topicName;

    @Override
    public void publishRequested(UUID reservationId, UUID eventId, UUID clientId) {
        log.info("Publishing reservation REQUESTED event to Kafka: reservationId={}, eventId={}, clientId={}", reservationId, eventId, clientId);
        publishEmailNotification(
                reservationId,
                "customer@" + clientId + ".com",
                "Reserva de Ingresso Solicitada",
                String.format("Sua solicitação de reserva %s para o evento %s foi recebida com sucesso.", reservationId, eventId)
        );
    }

    @Override
    public void publishCreated(UUID reservationId, UUID clientId, String invoicePdfUrl) {
        log.info("Publishing reservation CREATED event to Kafka: reservationId={}, clientId={}, invoicePdfUrl={}", reservationId, clientId, invoicePdfUrl);
        publishEmailNotification(
                reservationId,
                "customer@" + clientId + ".com",
                "Reserva Confirmada",
                String.format("Sua reserva %s foi criada. Fatura disponível em: %s", reservationId, invoicePdfUrl)
        );
    }

    @Override
    public void publishExpiredDelay(UUID reservationId, UUID eventId, long delayMinutes) {
        log.info("Publishing delayed EXPIRED event to Kafka: reservationId={}, eventId={}, delayMinutes={}", reservationId, eventId, delayMinutes);
        publishEmailNotification(
                reservationId,
                "management@ticket.com",
                "Reserva Expirada",
                String.format("A reserva %s do evento %s expirou após %d minutos sem pagamento.", reservationId, eventId, delayMinutes)
        );
    }

    @Override
    public void publishFinished(UUID reservationId, UUID clientId) {
        log.info("Publishing reservation FINISHED/PAYED event to Kafka: reservationId={}, clientId={}", reservationId, clientId);
        publishEmailNotification(
                reservationId,
                "customer@" + clientId + ".com",
                "Pagamento Confirmado",
                String.format("O pagamento da sua reserva %s foi confirmado com sucesso.", reservationId)
        );
    }

    private void publishEmailNotification(UUID externalId, String recipient, String subject, String message) {
        var notificationMsg = NotificationMsg.builder()
                .externalId(externalId)
                .email(EmailMsg.builder()
                        .recipient(recipient)
                        .subject(subject)
                        .message(message)
                        .build())
                .build();
        sendNotification(notificationMsg);
    }

    private void sendNotification(NotificationMsg notificationMsg) {
        if (notificationMsg == null) {
            return;
        }
        log.info("Publishing notification message to topic {}: {}", topicName, notificationMsg.getExternalId());
        kafkaTemplate.send(topicName, notificationMsg.getExternalId() != null ? notificationMsg.getExternalId().toString() : null, notificationMsg);
    }
}
