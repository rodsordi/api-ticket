package br.com.cielo.ticket.domain.repository;

import br.com.cielo.ticket.domain.entity.Event;

import java.util.Optional;
import java.util.UUID;

public interface EventRepository {

    Event save(Event event);

    Optional<Event> findById(UUID id);

    void deleteById(UUID id);
}
