package br.com.cielo.ticket.domain.entity.factory;

import br.com.cielo.ticket.domain.entity.Reservation;
import br.com.cielo.ticket.domain.entity.enums.ReservationStatus;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

import static br.com.cielo.ticket.domain.entity.factory.ClientFactory.create_Client;
import static java.util.UUID.fromString;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public final class ReservationFactory {

    private final Reservation.ReservationBuilder<?, ?> builder;

    public static ReservationFactory create_Reservation() {
        return new ReservationFactory(Reservation.builder());
    }

    public Reservation withAllFields() {
        return builder
                .id(fromString("767e1d48-9986-4559-8eed-4db955d9e757"))
                .status(ReservationStatus.REQUESTED)
                .invoicePdfUrl("https://s3.amazonaws.com/invoices/inv-123.pdf")
                .client(create_Client().withAllFields())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public Reservation valid() {
        return builder
                .id(UUID.randomUUID())
                .status(ReservationStatus.REQUESTED)
                .client(create_Client().valid())
                .build();
    }

    public Reservation initiatedEmpty() {
        return builder.build();
    }
}
