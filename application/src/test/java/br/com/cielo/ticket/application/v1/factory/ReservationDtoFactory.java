package br.com.cielo.ticket.application.v1.factory;

import br.com.cielo.ticket.application.v1.dto.ReservationDto;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public final class ReservationDtoFactory {

    public static ReservationDtoFactory create_ReservationDto() {
        return new ReservationDtoFactory();
    }

    public ReservationDto.ReserveRequest valid(UUID eventId) {
        return validBuilder(eventId).build();
    }

    public ReservationDto.ReserveRequest.ReserveRequestBuilder validBuilder(UUID eventId) {
        return ReservationDto.ReserveRequest.builder()
                .eventId(eventId);
    }
}
