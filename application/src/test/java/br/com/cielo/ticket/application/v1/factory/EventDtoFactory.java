package br.com.cielo.ticket.application.v1.factory;

import br.com.cielo.ticket.application.v1.dto.EventDto;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public final class EventDtoFactory {

    public static EventDtoFactory create_EventDto() {
        return new EventDtoFactory();
    }

    public EventDto.CreateRequest valid() {
        return validBuilder().build();
    }

    public EventDto.CreateRequest.CreateRequestBuilder validBuilder() {
        return EventDto.CreateRequest.builder()
                .name("Rock in Rio 2026")
                .description("Music Festival")
                .price(new BigDecimal("350.00"))
                .availableQuantity(10000)
                .eventDate(LocalDate.now().plusMonths(2))
                .totalQuantity(10000);
    }
}
