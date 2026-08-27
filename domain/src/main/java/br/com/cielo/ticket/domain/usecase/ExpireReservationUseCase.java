package br.com.cielo.ticket.domain.usecase;

import br.com.cielo.commons.exception.ResourceNotFoundException;
import br.com.cielo.ticket.domain.entity.Reservation;
import br.com.cielo.ticket.domain.entity.enums.ReservationStatus;
import br.com.cielo.ticket.domain.repository.EventAvailabilityCacheRepository;
import br.com.cielo.ticket.domain.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class ExpireReservationUseCase {

    private final ReservationRepository reservationRepository;
    private final EventAvailabilityCacheRepository availabilityCacheRepository;

    public Reservation execute(UUID reservationId, UUID eventId) {
        var reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException(Reservation.class, "id", reservationId));

        if (reservation.getStatus() == ReservationStatus.REQUESTED || reservation.getStatus() == ReservationStatus.AWAITING_PAYMENT) {
            reservation.expire();
            var expiredReservation = reservationRepository.save(reservation);

            availabilityCacheRepository.increment(eventId);
            return expiredReservation;
        }

        return reservation;
    }
}
