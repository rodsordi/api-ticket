package br.com.cielo.ticket.domain.repository;

import br.com.cielo.ticket.domain.entity.Event;
import br.com.cielo.ticket.domain.entity.enums.EventStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventRepository {

    Event save(Event event);

    Optional<Event> findById(UUID id);

    List<Event> findByStatus(EventStatus status);

    void deleteById(UUID id);
}
