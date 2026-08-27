package br.com.cielo.ticket.domain.usecase;

import br.com.cielo.commons.util.ValidatorUtils;
import br.com.cielo.ticket.domain.entity.Event;
import br.com.cielo.ticket.domain.repository.EventAvailabilityCacheRepository;
import br.com.cielo.ticket.domain.repository.EventRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateEventUseCase {

    private final EventRepository eventRepository;
    private final EventAvailabilityCacheRepository availabilityCacheRepository;

    public Event execute(Event event, int totalTickets) {
        ValidatorUtils.validate(event);

        var savedEvent = eventRepository.save(event);
        availabilityCacheRepository.initAvailability(savedEvent.getId(), totalTickets);
        return savedEvent;
    }
}
