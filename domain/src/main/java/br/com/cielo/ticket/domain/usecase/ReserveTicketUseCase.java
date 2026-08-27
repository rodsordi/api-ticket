package br.com.cielo.ticket.domain.usecase;

import br.com.cielo.commons.exception.BusinessException;
import br.com.cielo.commons.exception.ResourceNotFoundException;
import br.com.cielo.commons.util.ValidatorUtils;
import br.com.cielo.ticket.domain.entity.Client;
import br.com.cielo.ticket.domain.entity.Reservation;
import br.com.cielo.ticket.domain.entity.enums.ReservationStatus;
import br.com.cielo.ticket.domain.repository.EventAvailabilityCacheRepository;
import br.com.cielo.ticket.domain.repository.EventRepository;
import br.com.cielo.ticket.domain.repository.PaymentGatewayRepository;
import br.com.cielo.ticket.domain.repository.ReservationEventPublisherRepository;
import br.com.cielo.ticket.domain.repository.ReservationRepository;
import br.com.cielo.ticket.domain.repository.S3StorageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.util.UUID;

@RequiredArgsConstructor
public class ReserveTicketUseCase {

    private final ReservationRepository reservationRepository;
    private final EventRepository eventRepository;
    private final PaymentGatewayRepository paymentGatewayRepository;
    private final S3StorageRepository s3StorageRepository;
    private final ReservationEventPublisherRepository eventPublisherRepository;
    private final EventAvailabilityCacheRepository availabilityCacheRepository;

    @Value("${reservation.expiration-delay-minutes:15}")
    private final long expirationDelayMinutes;

    public UUID request(UUID eventId, Client client) {
        ValidatorUtils.validate(client);

        var hasStock = availabilityCacheRepository.tryDecrement(eventId);
        if (!hasStock) {
            throw new BusinessException(String.format("Ingressos esgotados ou indisponíveis para o evento %s", eventId));
        }

        var reservationId = UUID.randomUUID();
        eventPublisherRepository.publishRequested(reservationId, eventId, client.getId());
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

        var pdfBytes = paymentGatewayRepository.generateInvoicePdf(reservationId, client.getId(), event.getPrice());
        var invoicePdfUrl = s3StorageRepository.uploadInvoicePdf(reservationId, pdfBytes);

        savedReservation.markAwaitingPayment(invoicePdfUrl);

        var finalReservation = reservationRepository.save(savedReservation);

        eventPublisherRepository.publishCreated(reservationId, client.getId(), invoicePdfUrl);
        eventPublisherRepository.publishExpiredDelay(reservationId, eventId, expirationDelayMinutes);

        return finalReservation;
    }
}
