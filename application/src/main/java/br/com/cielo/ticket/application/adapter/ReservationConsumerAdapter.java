package br.com.cielo.ticket.application.adapter;

import br.com.cielo.ticket.application.v1.msg.NotificationMsg;
import br.com.cielo.ticket.domain.entity.Client;
import br.com.cielo.ticket.domain.usecase.PayReservationUseCase;
import br.com.cielo.ticket.domain.usecase.ReserveTicketUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationConsumerAdapter {

    private final ReserveTicketUseCase reserveTicketUseCase;
    private final PayReservationUseCase payReservationUseCase;

    @KafkaListener(
            topics = "${message.notification-creation.topic:api-ticket_notification-creation_topic}",
            groupId = "${spring.kafka.consumer.group-id:api-ticket-group}"
    )
    public void consume(NotificationMsg message) {
        if (message == null || message.getExternalId() == null) {
            return;
        }

        UUID reservationId = message.getExternalId();
        log.info("Received Kafka message for reservationId: {}", reservationId);

        String subject = (message.getEmail() != null) ? message.getEmail().getSubject() : null;

        if ("Reserva de Ingresso Solicitada".equals(subject)) {
            try {
                String msgContent = message.getEmail().getMessage();
                String recipient = message.getEmail().getRecipient();
                String clientIdStr = recipient.replace("customer@", "").replace(".com", "");
                UUID clientId = UUID.fromString(clientIdStr);

                int eventIdIndex = msgContent.lastIndexOf(" para o evento ");
                int endOffset = msgContent.indexOf(" foi recebida");
                String eventIdStr = msgContent.substring(eventIdIndex + " para o evento ".length(), endOffset);
                UUID eventId = UUID.fromString(eventIdStr);

                Client client = Client.builder()
                        .id(clientId)
                        .fullName("Jane Smith")
                        .document("98765432100")
                        .email("jane.smith@cielo.com.br")
                        .birthDate(LocalDate.now().minusYears(30))
                        .build();

                log.info("Processing requested reservation via Kafka Consumer: reservationId={}, eventId={}", reservationId, eventId);
                reserveTicketUseCase.processRequested(reservationId, eventId, client);
            } catch (Exception e) {
                log.error("Error processing REQUESTED reservation message for reservationId {}: {}", reservationId, e.getMessage(), e);
            }
        } else if (subject == null || "Pagamento de Reserva".equals(subject)) {
            try {
                log.info("Processing payment via Kafka Consumer for reservationId: {}", reservationId);
                payReservationUseCase.execute(reservationId);
            } catch (Exception e) {
                log.error("Error processing payment message for reservationId {}: {}", reservationId, e.getMessage(), e);
            }
        } else {
            log.info("Notification event received (subject={}), no state transition required for reservationId: {}", subject, reservationId);
        }
    }
}
