package br.com.cielo.ticket.domain.usecase;

import br.com.cielo.commons.exception.BusinessException;
import br.com.cielo.commons.exception.ResourceNotFoundException;
import br.com.cielo.commons.util.ValidatorUtils;
import br.com.cielo.ticket.domain.entity.Client;
import br.com.cielo.ticket.domain.entity.Reservation;
import br.com.cielo.ticket.domain.entity.enums.ReservationStatus;
import br.com.cielo.ticket.domain.port.EventAvailabilityCachePort;
import br.com.cielo.ticket.domain.port.PaymentGatewayPort;
import br.com.cielo.ticket.domain.port.ReservationEventPublisherPort;
import br.com.cielo.ticket.domain.port.S3StoragePort;
import br.com.cielo.ticket.domain.repository.EventRepository;
import br.com.cielo.ticket.domain.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.util.UUID;

@RequiredArgsConstructor
public class ReserveTicketUseCase {

    private final ReservationRepository reservationRepository;
    private final EventRepository eventRepository;
    private final PaymentGatewayPort paymentGatewayPort;
    private final S3StoragePort s3StoragePort;
    private final ReservationEventPublisherPort eventPublisherPort;
    private final EventAvailabilityCachePort availabilityCachePort;

    @Value("${reservation.expiration-delay-minutes:15}")
    private final long expirationDelayMinutes;

    public UUID request(UUID eventId, Client client) {
        ValidatorUtils.validate(client);

        var hasStock = availabilityCachePort.tryDecrement(eventId);
        if (!hasStock) {
            throw new BusinessException(String.format("Ingressos esgotados ou indisponíveis para o evento %s", eventId));
        }

        var reservationId = UUID.randomUUID();
        eventPublisherPort.publishRequested(reservationId, eventId, client.getId());
        return reservationId;
    }

    public Reservation processRequested(UUID reservationId, UUID eventId, Client client) {
        ValidatorUtils.validate(client);

        var event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException(Reservation.class, "eventId", eventId));

        var reservation = Reservation.builder()
                .id(reservationId)
                .status(ReservationStatus.REQUESTED)
                .client(client)
                .build();

        ValidatorUtils.validate(reservation);

        var savedReservation = reservationRepository.save(reservation);

        var pdfBytes = paymentGatewayPort.generateInvoicePdf(reservationId, client.getId(), event.getPrice());
        var invoicePdfUrl = s3StoragePort.uploadInvoicePdf(reservationId, pdfBytes);

        savedReservation.markAwaitingPayment(invoicePdfUrl);

        var finalReservation = reservationRepository.save(savedReservation);

        eventPublisherPort.publishCreated(reservationId, client.getId(), invoicePdfUrl);
        eventPublisherPort.publishExpiredDelay(reservationId, eventId, expirationDelayMinutes);

        return finalReservation;
    }
}
