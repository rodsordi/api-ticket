package br.com.cielo.ticket.application.adapter;

import br.com.cielo.ticket.domain.port.ReservationEventPublisherPort;
import br.com.cielo.ticket.application.publisher.NotificationKafkaPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationEventPublisherAdapter implements ReservationEventPublisherPort {

    private final NotificationKafkaPublisher notificationKafkaPublisher;

    @Override
    public void publishRequested(UUID reservationId, UUID eventId, UUID clientId) {
        log.info("Publishing reservation REQUESTED event: reservationId={}, eventId={}, clientId={}", reservationId, eventId, clientId);
    }

    @Override
    public void publishCreated(UUID reservationId, UUID clientId, String invoicePdfUrl) {
        log.info("Publishing reservation CREATED event: reservationId={}, clientId={}, invoicePdfUrl={}", reservationId, clientId, invoicePdfUrl);
    }

    @Override
    public void publishExpiredDelay(UUID reservationId, UUID eventId, long delayMinutes) {
        log.info("Publishing delayed EXPIRED event: reservationId={}, eventId={}, delayMinutes={}", reservationId, eventId, delayMinutes);
    }

    @Override
    public void publishFinished(UUID reservationId, UUID clientId) {
        log.info("Publishing reservation FINISHED/PAYED event: reservationId={}, clientId={}", reservationId, clientId);
    }
}
