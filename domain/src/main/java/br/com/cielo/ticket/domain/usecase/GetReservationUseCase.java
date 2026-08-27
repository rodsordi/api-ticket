package br.com.cielo.ticket.domain.usecase;

import br.com.cielo.commons.exception.ResourceNotFoundException;
import br.com.cielo.ticket.domain.entity.Reservation;
import br.com.cielo.ticket.domain.entity.enums.ReservationStatus;
import br.com.cielo.ticket.domain.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetReservationUseCase {

    private final ReservationRepository reservationRepository;
    private final ExpireReservationUseCase expireReservationUseCase;

    @Value("${reservation.expiration-delay-seconds:900}")
    private long expirationDelaySeconds = 900;

    public Reservation execute(UUID reservationId) {
        var reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException(Reservation.class, "id", reservationId));

        if (expireReservationUseCase != null
                && reservation.getStatus() == ReservationStatus.AWAITING_PAYMENT
                && reservation.getCreatedAt() != null
                && reservation.getCreatedAt().plusSeconds(expirationDelaySeconds).isBefore(LocalDateTime.now())) {
            return expireReservationUseCase.execute(reservation.getId(), null);
        }

        return reservation;
    }
}
