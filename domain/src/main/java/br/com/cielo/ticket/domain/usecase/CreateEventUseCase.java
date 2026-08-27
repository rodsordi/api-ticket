package br.com.cielo.ticket.domain.usecase;

import br.com.cielo.commons.util.ValidatorUtils;
import br.com.cielo.ticket.domain.entity.Event;
import br.com.cielo.ticket.domain.repository.EventAvailabilityCachePort;
import br.com.cielo.ticket.domain.repository.EventRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateEventUseCase {

    private final EventRepository eventRepository;
    private final EventAvailabilityCachePort availabilityCachePort;

    public Event execute(Event event, int totalTickets) {
        ValidatorUtils.validate(event);

        var savedEvent = eventRepository.save(event);
        availabilityCachePort.initializeStock(savedEvent.getId(), totalTickets);

        return savedEvent;
    }
}
