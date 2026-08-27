package br.com.cielo.ticket.domain.repository;

import java.util.UUID;

public interface EventAvailabilityCachePort {
    void initializeStock(UUID eventId, int totalQuantity);
    boolean tryDecrement(UUID eventId);
    void increment(UUID eventId);
    int getStock(UUID eventId);
}
