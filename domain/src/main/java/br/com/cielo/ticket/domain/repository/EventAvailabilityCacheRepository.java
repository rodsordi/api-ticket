package br.com.cielo.ticket.domain.repository;

import java.util.UUID;

public interface EventAvailabilityCacheRepository {
    boolean isAvailable(UUID eventId);
    boolean tryDecrement(UUID eventId);
    void increment(UUID eventId);
    void initAvailability(UUID eventId, int totalTickets);
}
