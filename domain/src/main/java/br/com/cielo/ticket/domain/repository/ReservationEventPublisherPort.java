package br.com.cielo.ticket.domain.repository;

import java.util.UUID;

public interface ReservationEventPublisherPort {
    void publishRequested(UUID reservationId, UUID eventId, UUID clientId);
    void publishCreated(UUID reservationId, UUID clientId, String invoicePdfUrl);
    void publishExpiredDelay(UUID reservationId, UUID eventId, long delayMinutes);
    void publishFinished(UUID reservationId, UUID clientId);
}
