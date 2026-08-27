package br.com.cielo.ticket.domain.entity.factory;

import br.com.cielo.ticket.domain.entity.Event;
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
                .description("Festival de música")
                .price(new BigDecimal("150.00"))
                .availableQuantity(1000)
                .eventDate(LocalDate.now().plusMonths(6))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public Event valid() {
        return builder
                .id(UUID.randomUUID())
                .name("Lollapalooza")
                .description("Festival de música alternativa")
                .price(new BigDecimal("200.00"))
                .availableQuantity(500)
                .eventDate(LocalDate.now().plusMonths(3))
                .build();
    }

    public Event initiatedEmpty() {
        return builder.build();
    }
}
