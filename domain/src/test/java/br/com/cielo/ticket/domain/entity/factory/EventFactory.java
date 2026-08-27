package br.com.cielo.ticket.domain.entity.factory;

import br.com.cielo.ticket.domain.entity.Event;
import br.com.cielo.ticket.domain.entity.enums.EventStatus;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static java.util.UUID.fromString;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public final class EventFactory {

    private final Event.EventBuilder<?, ?> builder;

    public static EventFactory create_Event() {
        return new EventFactory(Event.builder());
    }

    public Event withAllFields() {
        return builder
                .id(fromString("1029deef-ed87-46c3-b345-a13b30168659"))
                .name("Rock in Rio")
                .status(EventStatus.OPENED_FOR_SALE)
                .description("Festival de música")
                .price(new BigDecimal("150.00"))
                .launchingDateTime(LocalDateTime.now().plusDays(1))
                .eventDate(LocalDate.now().plusMonths(6))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public Event valid() {
        return builder
                .id(UUID.randomUUID())
                .name("Lollapalooza")
                .status(EventStatus.WAITING_LAUNCHING_DATE)
                .description("Festival de música alternativa")
                .price(new BigDecimal("200.00"))
                .launchingDateTime(LocalDateTime.now().plusDays(2))
                .eventDate(LocalDate.now().plusMonths(3))
                .build();
    }

    public Event initiatedEmpty() {
        return builder.build();
    }
}
