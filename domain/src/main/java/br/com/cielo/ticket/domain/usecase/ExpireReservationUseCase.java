package br.com.cielo.ticket.domain.usecase;

import br.com.cielo.commons.exception.ResourceNotFoundException;
import br.com.cielo.ticket.domain.entity.Reservation;
import br.com.cielo.ticket.domain.port.EventAvailabilityCachePort;
import br.com.cielo.ticket.domain.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExpireReservationUseCase {

    private final ReservationRepository reservationRepository;
    private final EventAvailabilityCachePort availabilityCachePort;

    public Reservation execute(UUID reservationId, UUID eventId) {
        var reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException(Reservation.class, "id", reservationId));

        if (reservation.getStatus().isPending()) {
            reservation.expire();
            var expiredReservation = reservationRepository.save(reservation);
            availabilityCachePort.increment(eventId);
            return expiredReservation;
        }

        return reservation;
    }
}
