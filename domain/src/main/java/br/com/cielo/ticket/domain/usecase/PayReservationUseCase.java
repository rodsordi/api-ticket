package br.com.cielo.ticket.domain.usecase;

import br.com.cielo.commons.exception.ResourceNotFoundException;
import br.com.cielo.ticket.domain.entity.Reservation;
import br.com.cielo.ticket.domain.repository.ReservationEventPublisherPort;
import br.com.cielo.ticket.domain.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class PayReservationUseCase {

    private final ReservationRepository reservationRepository;
    private final ReservationEventPublisherPort eventPublisherPort;

    public Reservation execute(UUID reservationId) {
        var reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException(Reservation.class, "id", reservationId));

        reservation.pay();
        var payedReservation = reservationRepository.save(reservation);

        eventPublisherPort.publishFinished(reservationId, reservation.getClient().getId());
        return payedReservation;
    }
}
