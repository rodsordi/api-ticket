package br.com.cielo.ticket.domain.usecase;

import br.com.cielo.commons.exception.ResourceNotFoundException;
import br.com.cielo.ticket.domain.entity.Reservation;
import br.com.cielo.ticket.domain.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetReservationUseCase {

    private final ReservationRepository reservationRepository;

    public Reservation execute(UUID reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException(Reservation.class, "id", reservationId));
    }
}
